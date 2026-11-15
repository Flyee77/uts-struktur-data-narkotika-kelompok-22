package com.umm.strukturdata.service;

import com.umm.strukturdata.datastructure.ArrayStack;
import com.umm.strukturdata.datastructure.DynamicArray;
import com.umm.strukturdata.model.Putusan;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class PutusanService {
    private DynamicArray<Putusan> daftarPutusan;
    private ArrayStack<String> auditLog;
    private int lastSearchSteps;

    public PutusanService() {
        this.daftarPutusan = new DynamicArray<>(60);
        this.auditLog = new ArrayStack<>();
        this.lastSearchSteps = 0;
    }

    public DynamicArray<Putusan> getDaftarPutusan() {
        return daftarPutusan;
    }

    public ArrayStack<String> getAuditLog() {
        return auditLog;
    }

    public int getLastSearchSteps() {
        return lastSearchSteps;
    }

    // Fitur 1: Muat dataset dari CSV
    public int loadFromCsv(String filePath) throws IOException {
        DynamicArray<Putusan> loaded = CsvParser.loadFromCsv(filePath);
        this.daftarPutusan = loaded;
        auditLog.push("LOAD_CSV | Memuat " + loaded.size() + " data dari " + filePath);
        return loaded.size();
    }

    // Fitur 3a: Tambah data baru
    public void tambahPutusan(Putusan p) {
        if (p == null) {
            throw new IllegalArgumentException("Data putusan tidak boleh null");
        }
        if (cariByCaseId(p.getCaseId()) != null) {
            throw new IllegalArgumentException("Duplikat caseId terdeteksi: " + p.getCaseId());
        }
        daftarPutusan.add(p);
        auditLog.push("ADD | Menambahkan putusan " + p.getCaseId() + " (" + p.getNomorPerkara() + ")");
    }

    // Fitur 3b: Hapus data berdasarkan indeks
    public Putusan hapusPutusan(int index) {
        Putusan removed = daftarPutusan.remove(index);
        auditLog.push("REMOVE | Menghapus putusan " + removed.getCaseId() + " pada indeks " + index);
        return removed;
    }

    // Fitur 5a: Cari berdasarkan caseId (dengan hitung langkah)
    public Putusan cariByCaseId(String caseId) {
        lastSearchSteps = 0;
        if (caseId == null) return null;
        String target = caseId.trim();
        for (int i = 0; i < daftarPutusan.size(); i++) {
            lastSearchSteps++;
            Putusan p = daftarPutusan.get(i);
            if (p.getCaseId().equalsIgnoreCase(target)) {
                auditLog.push("SEARCH_ID | Cari '" + target + "' ditemukan (" + lastSearchSteps + " langkah)");
                return p;
            }
        }
        auditLog.push("SEARCH_ID | Cari '" + target + "' tidak ditemukan (" + lastSearchSteps + " langkah)");
        return null;
    }

    // Fitur 5b: Cari berdasarkan nomorPerkara (dengan hitung langkah)
    public Putusan cariByNomorPerkara(String nomorPerkara) {
        lastSearchSteps = 0;
        if (nomorPerkara == null) return null;
        String target = nomorPerkara.trim().toLowerCase();
        for (int i = 0; i < daftarPutusan.size(); i++) {
            lastSearchSteps++;
            Putusan p = daftarPutusan.get(i);
            if (p.getNomorPerkara().toLowerCase().contains(target)) {
                auditLog.push("SEARCH_NO | Cari '" + nomorPerkara + "' ditemukan (" + lastSearchSteps + " langkah)");
                return p;
            }
        }
        auditLog.push("SEARCH_NO | Cari '" + nomorPerkara + "' tidak ditemukan (" + lastSearchSteps + " langkah)");
        return null;
    }

    // Fitur 6: Filter gabungan
    public DynamicArray<Putusan> filterPutusan(String jenisFilter, double minBerat, double maxBerat,
                                               String pasalFilter, int minVonis, int maxVonis) {
        DynamicArray<Putusan> hasil = new DynamicArray<>();
        String jenisLower = (jenisFilter == null) ? "" : jenisFilter.trim().toLowerCase();
        String pasalLower = (pasalFilter == null) ? "" : pasalFilter.trim().toLowerCase();

        for (int i = 0; i < daftarPutusan.size(); i++) {
            Putusan p = daftarPutusan.get(i);

            boolean matchJenis = jenisLower.isEmpty() || p.getJenisNarkotika().toLowerCase().contains(jenisLower);
            boolean matchBeratMin = (minBerat < 0) || (p.getBeratBarangBuktiGram() >= minBerat);
            boolean matchBeratMax = (maxBerat < 0) || (p.getBeratBarangBuktiGram() <= maxBerat);
            boolean matchPasal = pasalLower.isEmpty() || p.getPasalTerbukti().toLowerCase().contains(pasalLower);
            boolean matchVonisMin = (minVonis < 0) || (p.getVonisBulan() >= minVonis);
            boolean matchVonisMax = (maxVonis < 0) || (p.getVonisBulan() <= maxVonis);

            if (matchJenis && matchBeratMin && matchBeratMax && matchPasal && matchVonisMin && matchVonisMax) {
                hasil.add(p);
            }
        }

        auditLog.push("FILTER | Hasil filter: " + hasil.size() + " putusan cocok");
        return hasil;
    }

    // Fitur 7: Agregasi statistik tanpa java.util
    public String hitungStatistik() {
        int n = daftarPutusan.size();
        if (n == 0) {
            return "Dataset kosong. Tidak ada statistik yang dapat dihitung.";
        }

        long totalVonis = 0;
        long totalDenda = 0;
        double totalBerat = 0.0;
        int minVonis = Integer.MAX_VALUE;
        int maxVonis = Integer.MIN_VALUE;

        // Array paralel untuk menghitung distribusi jenis narkotika tanpa HashMap
        String[] namaZat = new String[n];
        int[] jumlahZat = new int[n];
        int uniqueZatCount = 0;

        for (int i = 0; i < n; i++) {
            Putusan p = daftarPutusan.get(i);
            int v = p.getVonisBulan();
            totalVonis += v;
            totalDenda += p.getDendaRupiah();
            totalBerat += p.getBeratBarangBuktiGram();

            if (v < minVonis) minVonis = v;
            if (v > maxVonis) maxVonis = v;

            String zat = p.getJenisNarkotika();
            int foundIdx = -1;
            for (int j = 0; j < uniqueZatCount; j++) {
                if (namaZat[j].equalsIgnoreCase(zat)) {
                    foundIdx = j;
                    break;
                }
            }
            if (foundIdx >= 0) {
                jumlahZat[foundIdx]++;
            } else {
                namaZat[uniqueZatCount] = zat;
                jumlahZat[uniqueZatCount] = 1;
                uniqueZatCount++;
            }
        }

        double rataVonis = (double) totalVonis / n;
        StringBuilder sb = new StringBuilder();
        sb.append("=== STATISTIK PUTUSAN NARKOTIKA ===\n");
        sb.append("Total Perkara          : ").append(n).append(" putusan\n");
        sb.append(String.format("Rata-rata Vonis        : %.2f bulan\n", rataVonis));
        sb.append("Vonis Terpendek        : ").append(minVonis).append(" bulan\n");
        sb.append("Vonis Terpanjang       : ").append(maxVonis).append(" bulan\n");
        sb.append(String.format("Total Barang Bukti     : %.4f gram\n", totalBerat));
        sb.append(String.format("Total Denda Rupiah     : Rp%,d\n", totalDenda));
        sb.append("\nDistribusi Jenis Narkotika:\n");
        for (int i = 0; i < uniqueZatCount; i++) {
            sb.append(String.format("  - %-25s : %d perkara\n", namaZat[i], jumlahZat[i]));
        }

        auditLog.push("STATS | Menghitung statistik agregasi dari " + n + " putusan");
        return sb.toString();
    }

    // Fitur 8: Pengurutan (Insertion Sort pada salinan baru agar struktur utama tidak berubah)
    public DynamicArray<Putusan> urutkanByVonis(boolean ascending) {
        int n = daftarPutusan.size();
        DynamicArray<Putusan> salinan = new DynamicArray<>(Math.max(10, n));
        for (int i = 0; i < n; i++) {
            salinan.add(daftarPutusan.get(i));
        }

        for (int i = 1; i < n; i++) {
            Putusan key = salinan.get(i);
            int j = i - 1;
            while (j >= 0 && (ascending
                    ? salinan.get(j).getVonisBulan() > key.getVonisBulan()
                    : salinan.get(j).getVonisBulan() < key.getVonisBulan())) {
                salinan.set(j + 1, salinan.get(j));
                j--;
            }
            salinan.set(j + 1, key);
        }

        auditLog.push("SORT | Mengurutkan " + n + " putusan berdasarkan vonis (" + (ascending ? "ASC" : "DESC") + ")");
        return salinan;
    }

    // Fitur 10: Ekspor daftar putusan ke file CSV
    public void eksporKeCsv(DynamicArray<Putusan> dataEkspor, String outputPath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8))) {
            bw.write("caseId,nomorPerkara,pengadilan,tanggalPutusan,terdakwaKode,jenisNarkotika,beratBarangBuktiGram,pasalTerbukti,tuntutanBulan,vonisBulan,dendaRupiah");
            bw.newLine();
            for (int i = 0; i < dataEkspor.size(); i++) {
                Putusan p = dataEkspor.get(i);
                bw.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.4f,\"%s\",%d,%d,%d",
                        p.getCaseId(), p.getNomorPerkara(), p.getPengadilan(), p.getTanggalPutusan(),
                        p.getTerdakwaKode(), p.getJenisNarkotika(), p.getBeratBarangBuktiGram(),
                        p.getPasalTerbukti(), p.getTuntutanBulan(), p.getVonisBulan(), p.getDendaRupiah()));
                bw.newLine();
            }
        }
        auditLog.push("EXPORT | Mengekspor " + dataEkspor.size() + " data ke " + outputPath);
    }
}