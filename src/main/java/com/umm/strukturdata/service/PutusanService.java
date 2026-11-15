package com.umm.strukturdata.service;

import com.umm.strukturdata.datastructure.ArrayStack;
import com.umm.strukturdata.datastructure.DynamicArray;
import com.umm.strukturdata.model.Putusan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PutusanService {
    private final DynamicArray<Putusan> daftarPutusan;
    private final ArrayStack<String> auditLog;
    private DynamicArray<Putusan> lastResult;
    private int lastSearchSteps;

    public PutusanService() {
        this.daftarPutusan = new DynamicArray<>();
        this.auditLog = new ArrayStack<>();
        this.lastResult = daftarPutusan;
        this.lastSearchSteps = 0;
    }

    private void logActivity(String action) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        auditLog.push("[" + time + "] " + action);
    }

    public void loadFromCsv(String filePath) throws IOException {
        daftarPutusan.clear();
        DynamicArray<Putusan> loaded = CsvParser.parsePutusanCsv(filePath);
        for (int i = 0; i < loaded.size(); i++) {
            daftarPutusan.add(loaded.get(i));
        }
        lastResult = daftarPutusan;
        logActivity("LOAD_CSV: Memuat " + daftarPutusan.size() + " putusan dari " + filePath);
    }

    public DynamicArray<Putusan> getDaftarPutusan() {
        return daftarPutusan;
    }

    public ArrayStack<String> getAuditLog() {
        return auditLog;
    }

    public DynamicArray<Putusan> getLastResult() {
        return lastResult;
    }

    public int getLastSearchSteps() {
        return lastSearchSteps;
    }

    public void tambahPutusan(Putusan p) {
        if (p == null || p.getCaseId() == null || p.getCaseId().trim().isEmpty()) {
            throw new IllegalArgumentException("Data putusan atau Case ID tidak boleh kosong.");
        }
        for (int i = 0; i < daftarPutusan.size(); i++) {
            if (daftarPutusan.get(i).getCaseId().equalsIgnoreCase(p.getCaseId().trim())) {
                throw new IllegalArgumentException("Duplikat Case ID terdeteksi: " + p.getCaseId());
            }
        }
        daftarPutusan.add(p);
        lastResult = daftarPutusan;
        logActivity("ADD: Menambah putusan " + p.getCaseId() + " (" + p.getNomorPerkara() + ")");
    }

    public Putusan hapusPutusan(int index) {
        Putusan removed = daftarPutusan.remove(index);
        lastResult = daftarPutusan;
        logActivity("REMOVE: Menghapus putusan indeks " + index + " (" + removed.getCaseId() + ")");
        return removed;
    }

    public Putusan cariByCaseId(String caseId) {
        lastSearchSteps = 0;
        if (caseId == null) {
            logActivity("SEARCH_ID: Pencarian null (0 langkah)");
            return null;
        }
        String target = caseId.trim();
        for (int i = 0; i < daftarPutusan.size(); i++) {
            lastSearchSteps++;
            Putusan p = daftarPutusan.get(i);
            if (p.getCaseId().equalsIgnoreCase(target)) {
                logActivity("SEARCH_ID: Ditemukan '" + target + "' dalam " + lastSearchSteps + " langkah");
                return p;
            }
        }
        logActivity("SEARCH_ID: Tidak ditemukan '" + target + "' setelah " + lastSearchSteps + " langkah");
        return null;
    }

    public Putusan cariByNomorPerkara(String nomorPerkara) {
        lastSearchSteps = 0;
        if (nomorPerkara == null) {
            logActivity("SEARCH_NO: Pencarian null (0 langkah)");
            return null;
        }
        String target = nomorPerkara.trim().toLowerCase();
        for (int i = 0; i < daftarPutusan.size(); i++) {
            lastSearchSteps++;
            Putusan p = daftarPutusan.get(i);
            if (p.getNomorPerkara().toLowerCase().contains(target)) {
                logActivity("SEARCH_NO: Ditemukan '" + nomorPerkara + "' dalam " + lastSearchSteps + " langkah");
                return p;
            }
        }
        logActivity("SEARCH_NO: Tidak ditemukan '" + nomorPerkara + "' setelah " + lastSearchSteps + " langkah");
        return null;
    }

    public DynamicArray<Putusan> filterPutusan(String jenis, double minBerat, double maxBerat,
                                               String pasal, int minVonis, int maxVonis) {
        DynamicArray<Putusan> hasil = new DynamicArray<>();
        String jenisFilter = (jenis == null) ? "" : jenis.trim().toLowerCase();
        String pasalFilter = (pasal == null) ? "" : pasal.trim().toLowerCase();

        for (int i = 0; i < daftarPutusan.size(); i++) {
            Putusan p = daftarPutusan.get(i);
            boolean matchJenis = jenisFilter.isEmpty() || p.getJenisNarkotika().toLowerCase().contains(jenisFilter);
            boolean matchMinBerat = (minBerat < 0) || p.getBeratBarangBuktiGram() >= minBerat;
            boolean matchMaxBerat = (maxBerat < 0) || p.getBeratBarangBuktiGram() <= maxBerat;
            boolean matchPasal = pasalFilter.isEmpty() || p.getPasalTerbukti().toLowerCase().contains(pasalFilter);
            boolean matchMinVonis = (minVonis < 0) || p.getVonisBulan() >= minVonis;
            boolean matchMaxVonis = (maxVonis < 0) || p.getVonisBulan() <= maxVonis;

            if (matchJenis && matchMinBerat && matchMaxBerat && matchPasal && matchMinVonis && matchMaxVonis) {
                hasil.add(p);
            }
        }
        lastResult = hasil;
        logActivity("FILTER: Ditemukan " + hasil.size() + " putusan sesuai kriteria");
        return hasil;
    }

    public DynamicArray<Putusan> urutkanByVonis(boolean ascending) {
        int n = daftarPutusan.size();
        Putusan[] copy = new Putusan[n];
        for (int i = 0; i < n; i++) {
            copy[i] = daftarPutusan.get(i);
        }

        for (int i = 1; i < n; i++) {
            Putusan key = copy[i];
            int j = i - 1;
            while (j >= 0 && (ascending
                    ? copy[j].getVonisBulan() > key.getVonisBulan()
                    : copy[j].getVonisBulan() < key.getVonisBulan())) {
                copy[j + 1] = copy[j];
                j--;
            }
            copy[j + 1] = key;
        }

        DynamicArray<Putusan> sorted = new DynamicArray<>(n > 0 ? n : 10);
        for (int i = 0; i < n; i++) {
            sorted.add(copy[i]);
        }
        lastResult = sorted;
        logActivity("SORT_VONIS: Mengurutkan " + n + " putusan (" + (ascending ? "ASC" : "DESC") + ")");
        return sorted;
    }

    public String getRingkasanStatistik() {
        int n = daftarPutusan.size();
        if (n == 0) {
            return "Dataset kosong. Tidak ada statistik yang dapat dihitung.";
        }

        long totalVonis = 0;
        int minVonis = Integer.MAX_VALUE;
        int maxVonis = Integer.MIN_VALUE;
        double totalBerat = 0.0;
        long totalDenda = 0;

        String[] kategori = new String[n];
        int[] jumlahKategori = new int[n];
        int jumlahUnik = 0;

        for (int i = 0; i < n; i++) {
            Putusan p = daftarPutusan.get(i);
            int v = p.getVonisBulan();
            totalVonis += v;
            if (v < minVonis) minVonis = v;
            if (v > maxVonis) maxVonis = v;
            totalBerat += p.getBeratBarangBuktiGram();
            totalDenda += p.getDendaRupiah();

            String jn = p.getJenisNarkotika();
            int foundIdx = -1;
            for (int k = 0; k < jumlahUnik; k++) {
                if (kategori[k].equalsIgnoreCase(jn)) {
                    foundIdx = k;
                    break;
                }
            }
            if (foundIdx != -1) {
                jumlahKategori[foundIdx]++;
            } else {
                kategori[jumlahUnik] = jn;
                jumlahKategori[jumlahUnik] = 1;
                jumlahUnik++;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== RINGKASAN STATISTIK PUTUSAN ===\n");
        sb.append(String.format("Total Perkara          : %d putusan\n", n));
        sb.append(String.format("Rata-rata Lama Vonis   : %.2f bulan\n", (double) totalVonis / n));
        sb.append(String.format("Vonis Terendah         : %d bulan\n", minVonis));
        sb.append(String.format("Vonis Tertinggi        : %d bulan\n", maxVonis));
        sb.append(String.format("Total Berat Barang Bukti: %.4f gram\n", totalBerat));
        sb.append(String.format("Total Denda            : Rp%,d\n", totalDenda));
        sb.append("\nDistribusi Jenis Narkotika:\n");
        for (int k = 0; k < jumlahUnik; k++) {
            sb.append(String.format(" - %-25s : %d perkara\n", kategori[k], jumlahKategori[k]));
        }

        logActivity("STATS: Menghitung agregasi statistik dari " + n + " putusan");
        return sb.toString();
    }

    public void exportToCsv(String outputPath, DynamicArray<Putusan> data) throws IOException {
        File file = new File(outputPath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("caseId,nomorPerkara,pengadilan,tanggalPutusan,terdakwaKode,jenisNarkotika,beratBarangBuktiGram,pasalDakwaan,pasalTerbukti,tuntutanBulan,vonisBulan,dendaRupiah,barangBukti,keadaanMemberatkan,keadaanMeringankan,sourcePages");
            bw.newLine();
            for (int i = 0; i < data.size(); i++) {
                Putusan p = data.get(i);
                bw.write(String.format("%s,\"%s\",\"%s\",%s,%s,\"%s\",%.4f,\"%s\",\"%s\",%d,%d,%d,\"%s\",\"%s\",\"%s\",\"%s\"",
                        p.getCaseId(), p.getNomorPerkara(), p.getPengadilan(), p.getTanggalPutusan(),
                        p.getTerdakwaKode(), p.getJenisNarkotika(), p.getBeratBarangBuktiGram(),
                        p.getPasalDakwaan(), p.getPasalTerbukti(), p.getTuntutanBulan(),
                        p.getVonisBulan(), p.getDendaRupiah(), p.getBarangBukti(),
                        p.getKeadaanMemberatkan(), p.getKeadaanMeringankan(), p.getSourcePages()));
                bw.newLine();
            }
        }
        logActivity("EXPORT_CSV: Mengekspor " + data.size() + " putusan ke " + outputPath);
    }
}
