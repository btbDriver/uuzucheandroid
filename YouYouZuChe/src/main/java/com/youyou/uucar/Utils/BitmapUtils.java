package com.youyou.uucar.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.View;

import java.io.IOException;

/**
 * Created by storm on 14-6-17.
 */
public class BitmapUtils {
    public static Bitmap drawViewToBitmap(View view, int width, int height, int downSampling) {
        return drawViewToBitmap(view, width, height, 0f, 0f, downSampling);
    }

    public static Bitmap drawViewToBitmap(View view, int width, int height, float translateX,
                                          float translateY, int downSampling) {
        float scale = 1f / downSampling;
        int bmpWidth = (int) (width * scale - translateX / downSampling);
        int bmpHeight = (int) (height * scale - translateY / downSampling);
        Bitmap dest = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(dest);
        c.translate(-translateX / downSampling, -translateY / downSampling);
        if (downSampling > 1) {
            c.scale(scale, scale);
        }
        view.draw(c);
        return dest;
    }


    public static Bitmap getInSampleBitmap(String filePath, int width, int height) {
        int widthNeed = (int) (width * DisplayUtil.density);
        int heightNeed = (int) (height * DisplayUtil.density);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        int originalWidth = options.outWidth;
        int originalHight = options.outHeight;

        int inSample;
        if (originalWidth >= originalHight) {
            inSample = getInsampleSize(originalWidth, widthNeed);
        } else {
            inSample = getInsampleSize(originalHight, heightNeed);
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSample;

        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap getInSampleBitmapByBitmap(Bitmap bitmap) {
        int oldWith = bitmap.getWidth();
        int oldLength = bitmap.getHeight();
        if (oldWith >= oldLength) {

            if (oldWith >= 3000) {
                oldWith = oldWith / 4;
                oldLength = oldLength / 4;
            } else if (oldWith >= 1500) {
                oldWith = oldWith / 2;
                oldLength = oldLength / 2;
            }
        } else {
            if (oldLength >= 3000) {
                oldWith = oldWith / 4;
                oldLength = oldLength / 4;
            } else if (oldLength >= 1500) {
                oldWith = oldWith / 2;
                oldLength = oldLength / 2;
            }
        }
        Bitmap newbitmap = zoomImage(bitmap, oldWith, oldLength);
//        if (bitmap != null) {
//            bitmap.recycle();
//        }
        return newbitmap;
    }

    public static Bitmap getInSampleAvatarByBitmap(Bitmap bitmap) {
        int oldWith = bitmap.getWidth();
        int oldLength = bitmap.getHeight();
        if (oldWith >= oldLength) {
            if (oldWith >= 4000) {
                oldWith = oldWith / 10;
                oldLength = oldLength / 10;
            } else if (oldWith >= 3000) {
                oldWith = oldWith / 8;
                oldLength = oldLength / 8;
            } else if (oldWith >= 2000) {
                oldWith = oldWith / 6;
                oldLength = oldLength / 6;
            } else if (oldWith >= 1200) {
                oldWith = oldWith / 4;
                oldLength = oldLength / 4;
            } else if (oldWith >= 600) {
                oldWith = oldWith / 2;
                oldLength = oldLength / 2;
            }
        } else {
            if (oldLength >= 4000) {
                oldWith = oldWith / 10;
                oldLength = oldLength / 10;
            } else if (oldLength >= 3000) {
                oldWith = oldWith / 8;
                oldLength = oldLength / 8;
            } else if (oldLength >= 2000) {
                oldWith = oldWith / 6;
                oldLength = oldLength / 6;
            } else if (oldLength >= 1200) {
                oldWith = oldWith / 4;
                oldLength = oldLength / 4;
            } else if (oldLength >= 600) {
                oldWith = oldWith / 2;
                oldLength = oldLength / 2;
            }
        }
        Bitmap newbitmap = zoomImage(bitmap, oldWith, oldLength);
//        if (bitmap != null) {
//            bitmap.recycle();
//        }
        return newbitmap;
    }

    public static Bitmap getInSampleBitmapByFile(String file) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        int oldWith = options.outWidth;
        int oldLength = options.outHeight;
        int inSample = 1;
        if (oldWith >= oldLength) {

            if (oldWith >= 3000) {
                inSample = 4;
            } else if (oldWith >= 1500) {
                inSample = 2;
            }
        } else {
            if (oldLength >= 3000) {
                inSample = 4;
            } else if (oldLength >= 1500) {
                inSample = 2;
            }
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSample;

        return BitmapFactory.decodeFile(file, options);
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    private static int getInsampleSize(int origin, int need) {
        int inSample = 1;
        int size = origin / need;
        if (size >= 16) {
            inSample = 16;
        } else if (size >= 8) {
            inSample = 8;
        } else if (size >= 4) {
            inSample = 4;
        } else if (size >= 2) {
            inSample = 2;
        }

        return inSample;
    }

    public static int getBitmapDegree(String path, int degreeInt) throws IOException {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException ex) {
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1 && orientation != 0) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            } else if (orientation == 0 && degreeInt != 0) {
                switch (degreeInt) {
                    case 90:
                        orientation = ExifInterface.ORIENTATION_ROTATE_90;
                        degree = 90;
                        break;
                    case 180:
                        orientation = ExifInterface.ORIENTATION_ROTATE_180;
                        degree = 180;
                        break;
                    case 270:
                        orientation = ExifInterface.ORIENTATION_ROTATE_270;
                        degree = 270;
                        break;
                }
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, Integer.toString(orientation));
                try {
                    exif.saveAttributes();
                } catch (IOException e) {
                }
            }

        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }

        if (bm != null && bm != returnBm) {
            bm.recycle();
        }

        return returnBm;
    }
}
