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
      config.put("cloud_name", "dcswmxc9i");
      config.put("api_key", "763453742235384");
      config.put("api_secret", "5TCQFWlBdvJt-9ITJPIXii3-Mh8");
      MediaManager.init(context, config);
      initialized = true;
    }
  }
}
