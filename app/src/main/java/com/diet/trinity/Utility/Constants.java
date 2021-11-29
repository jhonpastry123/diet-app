package com.diet.trinity.Utility;

import com.google.android.gms.wallet.WalletConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Constants {

    public static final int DEFAULT_PAGE_SIZE = 15;
    public static boolean USE_FAKE_SERVER = false;
    public static final String BASIC_SKU = "basic_subscription";
    public static final String PREMIUM_SKU = "premium_subscription";
    public static final String PLAY_STORE_SUBSCRIPTION_URL
            = "https://play.google.com/store/account/subscriptions";
    public static final String PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL
            = "https://play.google.com/store/account/subscriptions?sku=%s&package=%s";
    public static final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_PRODUCTION;
    public static final List<String> SUPPORTED_NETWORKS = Arrays.asList(
            "AMEX",
            "DISCOVER",
            "INTERAC",
            "JCB",
            "MASTERCARD",
            "VISA");
    public static final List<String> SUPPORTED_METHODS = Arrays.asList(
            "PAN_ONLY",
            "CRYPTOGRAM_3DS");
    public static final String COUNTRY_CODE = "US";
    public static final String CURRENCY_CODE = "USD";
    public static final List<String> SHIPPING_SUPPORTED_COUNTRIES = Arrays.asList("US", "GB");
    public static final String PAYMENT_GATEWAY_TOKENIZATION_NAME = "allpayments";
    public static final HashMap<String, String> PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS =
            new HashMap<String, String>() {{
                put("gateway", PAYMENT_GATEWAY_TOKENIZATION_NAME);
                put("gatewayMerchantId", "BCR2DN6T56NY5YCA");
                // Your processor may require additional parameters.
            }};
    public static final String DIRECT_TOKENIZATION_PUBLIC_KEY = "REPLACE_ME";
    public static final HashMap<String, String> DIRECT_TOKENIZATION_PARAMETERS =
            new HashMap<String, String>() {{
                put("protocolVersion", "ECv2");
                put("publicKey", DIRECT_TOKENIZATION_PUBLIC_KEY);
            }};
    public static final long TIMEOUT_CONNECT = TimeUnit.SECONDS.toMillis(30);
    public static final long TIMEOUT_READ = TimeUnit.MINUTES.toMillis(1);
    public static final long TIMEOUT_WRITE = TimeUnit.MINUTES.toMillis(10);
    public static final String PREF_SERVER_TOKEN = "server_token";
}