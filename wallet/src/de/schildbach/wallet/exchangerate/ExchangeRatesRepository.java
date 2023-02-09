/*
 * Copyright the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.apache.cordova.radiocoin.wallet.exchangerate;

import android.text.format.DateUtils;
import androidx.room.InvalidationTracker;
import com.google.common.base.Stopwatch;
import com.squareup.moshi.Moshi;
import org.apache.cordova.radiocoin.wallet.Configuration;
import org.apache.cordova.radiocoin.wallet.Constants;
import org.apache.cordova.radiocoin.wallet.WalletApplication;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Andreas Schildbach
 */
public class ExchangeRatesRepository {
    private static ExchangeRatesRepository INSTANCE;

    private static final long UPDATE_FREQ_MS = 10 * DateUtils.MINUTE_IN_MILLIS;
    private static final Logger log = LoggerFactory.getLogger(ExchangeRatesRepository.class);

    private final WalletApplication application;
    private final Configuration config;
    private final String userAgent;
    private final ExchangeRatesDatabase db;
    private final ExchangeRateDao dao;
    private final AtomicLong lastUpdated = new AtomicLong(0);

    public synchronized static ExchangeRatesRepository get(final WalletApplication application) {
        if (!Constants.ENABLE_EXCHANGE_RATES)
            return null;
        if (INSTANCE == null)
            INSTANCE = new ExchangeRatesRepository(application);
        return INSTANCE;
    }

    public ExchangeRatesRepository(final WalletApplication application) {
        this.application = application;
        this.config = application.getConfiguration();
        this.userAgent = WalletApplication.httpUserAgent(application.packageInfo().versionName);

        this.db = ExchangeRatesDatabase.getDatabase(application);
        this.dao = db.exchangeRateDao();
    }

    public ExchangeRateDao exchangeRateDao() {
        maybeRequestExchangeRates();
        return dao;
    }

    public InvalidationTracker exchangeRateInvalidationTracker() {
        return db.getInvalidationTracker();
    }

    private void requestDogeBtcConversion(DogeConversionCallback cb) {
        final Request.Builder request = new Request.Builder();
        request.url("https://api.coingecko.com/api/v3/simple/price?ids=dogecoin&vs_currencies=btc");
        final Headers.Builder headers = new Headers.Builder();
        headers.add("User-Agent", userAgent);
        headers.add("Accept", "application/json");
        request.headers(headers.build());

        final OkHttpClient.Builder httpClientBuilder = Constants.HTTP_CLIENT.newBuilder();
        httpClientBuilder.connectionSpecs(Collections.singletonList(ConnectionSpec.RESTRICTED_TLS));
        final Call call = httpClientBuilder.build().newCall(request.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.warn("problem fetching doge btc conversion", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        log.info("fetched doge btc conversion");
                        double conv = new Moshi.Builder().build().adapter(DogeBtcConversionResponse.class).fromJson(response.body().source()).dogecoin.btc;
                        cb.done(conv);
                    } else {
                        log.warn("http status {} {} when fetching doge btc conversion", response.code(), response.message());
                    }
                } catch (final Exception x) {
                    log.warn("problem fetching doge btc conversion", x);
                }
            }
        });
    }

    private static class DogeBtcConversionResponse {
        public DogeBtcConversionEntry dogecoin;
    }

    private static class DogeBtcConversionEntry {
        public double btc;
    }

    private interface DogeConversionCallback {
        void done(double conv);
    }

    private void maybeRequestExchangeRates() {
        final Stopwatch watch = Stopwatch.createStarted();
        final long now = System.currentTimeMillis();

        final long lastUpdated = this.lastUpdated.get();
        if (lastUpdated != 0 && now - lastUpdated <= UPDATE_FREQ_MS)
            return;

        requestDogeBtcConversion(conv -> {
            final CoinGecko coinGecko = new CoinGecko(new Moshi.Builder().build());
            final Request.Builder request = new Request.Builder();
            request.url(coinGecko.url());
            final Headers.Builder headers = new Headers.Builder();
            headers.add("User-Agent", userAgent);
            headers.add("Accept", coinGecko.mediaType().toString());
            request.headers(headers.build());

            final OkHttpClient.Builder httpClientBuilder = Constants.HTTP_CLIENT.newBuilder();
            httpClientBuilder.connectionSpecs(Collections.singletonList(ConnectionSpec.RESTRICTED_TLS));
            final Call call = httpClientBuilder.build().newCall(request.build());
            call.enqueue(new Callback() {
                @Override
                public void onResponse(final Call call, final Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            for (final ExchangeRateEntry exchangeRate : coinGecko.parse(response.body().source(), conv))
                                dao.insertOrUpdate(exchangeRate);
                            ExchangeRatesRepository.this.lastUpdated.set(now);
                            watch.stop();
                            log.info("fetched exchange rates from {}, took {}", coinGecko.url(), watch);
                        } else {
                            log.warn("http status {} {} when fetching exchange rates from {}", response.code(),
                                    response.message(), coinGecko.url());
                        }
                    } catch (final Exception x) {
                        log.warn("problem fetching exchange rates from " + coinGecko.url(), x);
                    }
                }

                @Override
                public void onFailure(final Call call, final IOException x) {
                    log.warn("problem fetching exchange rates from " + coinGecko.url(), x);
                }
            });
        });
    }
}
