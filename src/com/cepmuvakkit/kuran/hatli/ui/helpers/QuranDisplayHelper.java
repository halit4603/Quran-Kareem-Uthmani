package com.cepmuvakkit.kuran.hatli.ui.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.widget.Toast;

import com.cepmuvakkit.kuran.hatli.data.QuranInfo;
import com.cepmuvakkit.kuran.hatli.util.ArabicStyle;
import com.cepmuvakkit.kuran.hatli.util.QuranFileUtils;
import com.cepmuvakkit.kuran.hatli.util.QuranSettings;
import com.cepmuvakkit.kuran.hatli.util.QuranUtils;
import com.cepmuvakkit.kuran.hatli.R;
public class QuranDisplayHelper {
   private static final String TAG = "QuranDisplayHelper";
   
  public static Bitmap getQuranPage(Context context,
                                     String widthParam, int page){
      Bitmap bitmap;

      String filename = QuranFileUtils.getPageFileName(page);
      bitmap = QuranFileUtils.getImageFromSD(context, widthParam, filename);
      if (bitmap == null) {
         android.util.Log.d(TAG, "failed to get " + page +
                 " with name " + filename + " from sd...");
         bitmap = QuranFileUtils.getImageFromWeb(context, filename);
      }
      return bitmap;
   }
   
   public static long displayMarkerPopup(Context context, int page,
                                         long lastPopupTime) {
      if (System.currentTimeMillis() - lastPopupTime < 3000)
         return lastPopupTime;
      int rub3 = QuranInfo.getRub3FromPage(page);
      if (rub3 == -1)
         return lastPopupTime;
      int hizb = (rub3 / 4) + 1;
      StringBuilder sb = new StringBuilder();

      if (rub3 % 8 == 0) {
         sb.append(context.getString(R.string.quran_juz2)).append(' ')
                 .append(QuranUtils.getLocalizedNumber(context,
                         (hizb / 2) + 1));
      }
      else {
         int remainder = rub3 % 4;
         if (remainder == 1){
            sb.append(context.getString(R.string.quran_rob3)).append(' ');
         }
         else if (remainder == 2){
            sb.append(context.getString(R.string.quran_nos)).append(' ');
         }
         else if (remainder == 3){
            sb.append(context.getString(R.string.quran_talt_arb3)).append(' ');
         }
         sb.append(context.getString(R.string.quran_hizb)).append(' ')
                 .append(QuranUtils.getLocalizedNumber(context, hizb));
      }

      String result = sb.toString();
      if (QuranSettings.isReshapeArabic(context)){
         result = ArabicStyle.reshape(context, result);
      }
      Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
      return System.currentTimeMillis();
   }
   
   public static PaintDrawable getPaintDrawable(int startX, int endX){
      PaintDrawable drawable = new PaintDrawable();
      drawable.setShape(new RectShape());
      drawable.setShaderFactory(getShaderFactory(startX, endX));
      return drawable;
   }

   public static ShapeDrawable.ShaderFactory
   getShaderFactory(final int startX, final int endX){
      return new ShapeDrawable.ShaderFactory(){

         @Override
         public Shader resize(int width, int height) {
            return new LinearGradient(startX, 0, endX, 0,
                  new int[]{ 0xFFDCDAD5, 0xFFFDFDF4,
                  0xFFFFFFFF, 0xFFFDFBEF },
                  new float[]{ 0, 0.18f, 0.48f, 1 },
                  Shader.TileMode.REPEAT);
         }
      };
   }
}
