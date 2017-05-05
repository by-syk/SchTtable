package com.by_syk.schttable.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by By_syk on 2016-08-30.
 */
public class ExtraUtil {
    public static boolean copyFile(InputStream inputStream, File targetFile) throws IOException {
        if (targetFile == null) {
            return false;
        }

        OutputStream outputStream = new FileOutputStream(targetFile);
        byte[] buffer = new byte[1024];
        while (inputStream.read(buffer) > 0) {
            outputStream.write(buffer);
        }
        outputStream.close();
        inputStream.close();
        return true;
    }

    public static boolean saveFile(String text, File saveFile) {
        if (text == null || saveFile == null) {
            return false;
        }

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(saveFile);
            outputStream.write(text.getBytes("UTF-8"));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public static String readFile(File file) {
        if (file == null) {
            return null;
        }

        StringBuilder sbData = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null) {
                sbData.append(buffer).append("\n");
            }
            if (sbData.length() > 0) {
                sbData.setLength(sbData.length() - 1);
            }

            return sbData.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static String readFile(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        StringBuilder sbData = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null) {
                sbData.append(buffer).append("\n");
            }
            if (sbData.length() > 0) {
                sbData.setLength(sbData.length() - 1);
            }

            return sbData.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static boolean unzip(File zipFile, File targetDir) {
        if (zipFile == null || !zipFile.exists() || targetDir == null) {
            return false;
        }
        targetDir.mkdirs();

        InputStream inputStream = null;
        ZipInputStream zis = null;
        try {
            inputStream = new FileInputStream(zipFile);
            zis = new ZipInputStream(inputStream);
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    continue;
                }
                File file = new File(targetDir, zipEntry.getName());
                file.getParentFile().mkdirs();
                OutputStream osEntity = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) != -1) {
                    osEntity.write(buffer, 0, len);
                }
                osEntity.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context, boolean isWifiOnly) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }

        boolean is_connected = networkInfo.isAvailable();
        if (isWifiOnly) {
            is_connected &= networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }

        return is_connected;
    }

    public static boolean isNetworkConnected(Context context) {
        return isNetworkConnected(context, false);
    }

    public static void sendEmail(Context context, String email, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", email, null));
        if (subject != null) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getReadableFileSize(int bytes) {
        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < 1024 * 1024) {
            return String.format(Locale.US, "%.0fKB", (bytes / 1024f));
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format(Locale.US, "%.2fMB", (bytes / (1024f * 1024f)));
        } else {
            return String.format(Locale.US, "%.2fGB", (bytes / (1024f * 1024f * 1024f)));
        }
    }

    public static int getAppVerCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static boolean clearDir(File dir) {
        if (dir == null || !dir.exists()) {
            return false;
        }
        boolean ok = true;
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                ok &= clearDir(file);
                continue;
            }
            ok &= file.delete();
        }
        return dir.delete();
    }
}
