package com.yog.androidarena.util;

import java.util.Collections;
import java.util.List;

public class Constants {
  public static final String LINK="link";
  public static final String EMAIL="email";
  public static final String SP="sp";
  public static final String NOT_NOW="not_now";
  public static final String MAP="map";
  public static final String RATE_COUNT="rate_count";
  public static final String RATED="rated";
  public static final String ADMOB_APP_ID="ca-app-pub-6674467650273820~8605738120";
  public static final String TEST_AD="ca-app-pub-3940256099942544/2247696110";
  public static final String BANNER_TEST_AD="ca-app-pub-3940256099942544~3347511713";
  public static final String CONTENT_URL="https://blog.mindorks.com/blogs/latest";
  public static final String  THINGS_EXPANSION = "ThingsYouShouldKnowExpansion";
  public static final String  THINGS_LIST = "ThingsList";

  //By default putting one value to avoid exception in Random().nextInt()
  // (When AdTypes are not loaded from firebase)
  public static List<String> AD_TYPES= Collections.singletonList("https://blog.mindorks.com/blogs/latest");
}
