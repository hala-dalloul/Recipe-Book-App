package com.example.recipebook;

import android.content.Context;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryLib {
  private static boolean initialized = false;

  public static void init(Context context) {
    if (!initialized) {
      Map config = new HashMap();
      config.put("cloud_name", "######");
      config.put("api_key", "######");
      config.put("api_secret", "#####");
      MediaManager.init(context, config);
      initialized = true;
    }
  }
}
