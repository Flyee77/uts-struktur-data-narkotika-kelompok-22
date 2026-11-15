package com.umm.strukturdata.service;

import com.umm.strukturdata.datastructure.DynamicArray;
import com.umm.strukturdata.model.Putusan;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CsvParser {

    public static DynamicArray<Putusan> loadFromCsv(String filePath) throws IOException {
        DynamicArray<Putusan> list = new DynamicArray<>(60);

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                // Lewati baris header pertama
                if (lineNumber == 1 && line.startsWith("caseId")) {
                    continue;
                }

                String[] cols = parseCsvLine(line);
                if (cols.length < 16) {
                    throw new IllegalArgumentException(
                            "Format kolom tidak lengkap pada baris ke-" + lineNumber + " (ditemukan " + cols.length + " kolom)");
                }

                Putusan p = new Putusan(
                        cols[0].trim(),
                        cols[1].trim(),
                        cols[2].trim(),
                        cols[3].trim(),
                        cols[4].trim(),
                        cols[5].trim(),
                        parseDoubleSafe(cols[6], lineNumber, "beratBarangBuktiGram"),
                        cols[7].trim(),
                        cols[8].trim(),
                        parseIntSafe(cols[9], lineNumber, "tuntutanBulan"),
                        parseIntSafe(cols[10], lineNumber, "vonisBulan"),
                        parseLongRupiahSafe(cols[11], lineNumber, "dendaRupiah"),
                        cols[12].trim(),
                        cols[13].trim(),
                        cols[14].trim(),
                        cols[15].trim()
                );
                list.add(p);
            }
        }
        return list;
    }

    public static String[] parseCsvLine(String line) {
        String[] temp = new String[20];
        int count = 0;
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                if (count == temp.length) {
                    String[] expanded = new String[temp.length * 2];
                    for (int j = 0; j < count; j++) expanded[j] = temp[j];
                    temp = expanded;
                }
                temp[count++] = current.toString();
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        if (count == temp.length) {
            String[] expanded = new String[temp.length + 1];
            for (int j = 0; j < count; j++) expanded[j] = temp[j];
            temp = expanded;
        }
        temp[count++] = current.toString();

        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = temp[i];
        }
        return result;
    }

    private static double parseDoubleSafe(String raw, int line, String field) {
        String clean = raw.trim().replace(",", ".");
        if (clean.isEmpty() || clean.equals("-") || clean.equalsIgnoreCase("nihil") || clean.equalsIgnoreCase("null")) {
            return 0.0;
        }
        // Jika terdapat lebih dari satu titik (misal format ribuan 2.098.015), pertahankan titik terakhir sebagai desimal
        int lastDot = clean.lastIndexOf('.');
        if (lastDot != -1 && clean.indexOf('.') != lastDot) {
            String beforeLastDot = clean.substring(0, lastDot).replace(".", "");
            String afterLastDot = clean.substring(lastDot);
            clean = beforeLastDot + afterLastDot;
        }
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Nilai desimal tidak valid pada baris " + line + " kolom " + field + ": " + raw);
        }
    }

    private static int parseIntSafe(String raw, int line, String field) {
        String clean = raw.trim();
        if (clean.isEmpty() || clean.equals("-") || clean.equalsIgnoreCase("nihil") || clean.equalsIgnoreCase("null")) {
            return 0;
        }
        try {
            return Integer.parseInt(clean);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Nilai bulat tidak valid pada baris " + line + " kolom " + field + ": " + raw);
        }
    }

    private static long parseLongRupiahSafe(String raw, int line, String field) {
        String clean = raw.trim().replace("Rp", "").replace("rp", "").replace(".", "").replace(",", "").trim();
        if (clean.isEmpty() || clean.equals("-") || clean.equalsIgnoreCase("nihil") || clean.equalsIgnoreCase("null")) {
            return 0L;
        }
        try {
            return Long.parseLong(clean);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Nilai rupiah tidak valid pada baris " + line + " kolom " + field + ": " + raw);
        }
    }
}