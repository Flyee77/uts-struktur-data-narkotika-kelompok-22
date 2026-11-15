package com.umm.strukturdata.app;

import com.umm.strukturdata.datastructure.ArrayStack;
import com.umm.strukturdata.datastructure.DynamicArray;
import com.umm.strukturdata.model.Putusan;
import com.umm.strukturdata.service.PutusanService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainApp {
    private static final BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    private static final PutusanService service = new PutusanService();
    private static DynamicArray<Putusan> hasilTerakhir;

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("  SISTEM PENGELOLAAN PUTUSAN NARKOTIKA (CUSTOM DATA STRUCTURES)  ");
        System.out.println("=================================================================");

        // Otomatis coba muat dataset bawaan saat startup
        try {
            int loaded = service.loadFromCsv("data/putusan-50.csv");
            hasilTerakhir = service.getDaftarPutusan();
            System.out.println("[INFO] Berhasil memuat awal " + loaded + " data dari data/putusan-50.csv");
        } catch (Exception e) {
            hasilTerakhir = service.getDaftarPutusan();
            System.out.println("[WARN] Gagal memuat otomatis data/putusan-50.csv: " + e.getMessage());
        }

        boolean running = true;
        while (running) {
            tampilkanMenu();
            String pilihan = bacaInput("Pilih menu (0-10): ");

            try {
                switch (pilihan) {
                    case "1":
                        menuMuatCsv();
                        break;
                    case "2":
                        tampilkanTabelPutusan(service.getDaftarPutusan());
                        hasilTerakhir = service.getDaftarPutusan();
                        break;
                    case "3":
                        menuTambahPutusan();
                        break;
                    case "4":
                        menuHapusPutusan();
                        break;
                    case "5":
                        menuCariPutusan();
                        break;
                    case "6":
                        menuFilterPutusan();
                        break;
                    case "7":
                        System.out.println("\n" + service.hitungStatistik());
                        break;
                    case "8":
                        menuUrutkanPutusan();
                        break;
                    case "9":
                        menuLihatAudit();
                        break;
                    case "10":
                        menuEksporData();
                        break;
                    case "0":
                        running = false;
                        System.out.println("Terima kasih. Program selesai.");
                        break;
                    default:
                        System.out.println("[ERROR] Pilihan tidak valid. Masukkan angka 0 sampai 10.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Terjadi kesalahan: " + e.getMessage());
            }
        }
    }

    private static void tampilkanMenu() {
        System.out.println("\n--- MENU UTAMA (Total Data: " + service.getDaftarPutusan().size() + ") ---");
        System.out.println("1. Muat Dataset dari CSV");
        System.out.println("2. Tampilkan Seluruh Ringkasan Putusan");
        System.out.println("3. Tambah Data Putusan Baru");
        System.out.println("4. Hapus Data Putusan (berdasarkan indeks)");
        System.out.println("5. Cari Putusan (caseId / nomorPerkara)");
        System.out.println("6. Filter Putusan (Zat, Berat, Pasal, Vonis)");
        System.out.println("7. Tampilkan Statistik Agregasi");
        System.out.println("8. Urutkan Putusan berdasarkan Vonis");
        System.out.println("9. Lihat Riwayat Audit (Stack LIFO)");
        System.out.println("10. Ekspor Data / Hasil Terakhir ke CSV");
        System.out.println("0. Keluar");
    }

    private static void menuMuatCsv() throws IOException {
        String path = bacaInput("Path file CSV (Enter untuk 'data/putusan-50.csv'): ");
        if (path.isEmpty()) path = "data/putusan-50.csv";
        int count = service.loadFromCsv(path);
        hasilTerakhir = service.getDaftarPutusan();
        System.out.println("[OK] Berhasil memuat " + count + " putusan. Validasi struktur: "
                + service.getDaftarPutusan().validateInvariant());
    }

    private static void tampilkanTabelPutusan(DynamicArray<Putusan> list) {
        if (list.isEmpty()) {
            System.out.println("[INFO] Tidak ada data untuk ditampilkan.");
            return;
        }
        System.out.println("\nIdx | CaseID  | Nomor Perkara          | Jenis Zat             | Berat(g) | Vonis | Denda");
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (int i = 0; i < list.size(); i++) {
            Putusan p = list.get(i);
            System.out.printf("%-3d | %-7s | %-22s | %-21s | %8.4f | %3d bln | Rp%,d%n",
                    i, p.getCaseId(), potongTeks(p.getNomorPerkara(), 22),
                    potongTeks(p.getJenisNarkotika(), 21), p.getBeratBarangBuktiGram(),
                    p.getVonisBulan(), p.getDendaRupiah());
        }
    }

    private static void menuTambahPutusan() {
        System.out.println("\n--- TAMBAH PUTUSAN BARU ---");
        String caseId = bacaInput("Case ID (misal PTS-051): ");
        String noPerkara = bacaInput("Nomor Perkara: ");
        String pengadilan = bacaInput("Pengadilan: ");
        String tanggal = bacaInput("Tanggal Putusan (DD/MM/YYYY): ");
        String terdakwa = bacaInput("Kode Terdakwa (misal TDW-051): ");
        String jenis = bacaInput("Jenis Narkotika: ");
        double berat = Double.parseDouble(bacaInput("Berat Barang Bukti (gram): "));
        String pasalDakwaan = bacaInput("Pasal Dakwaan: ");
        String pasalTerbukti = bacaInput("Pasal Terbukti: ");
        int tuntutan = Integer.parseInt(bacaInput("Tuntutan (bulan): "));
        int vonis = Integer.parseInt(bacaInput("Vonis (bulan): "));
        long denda = Long.parseLong(bacaInput("Denda (rupiah, angka saja): "));

        Putusan baru = new Putusan(caseId, noPerkara, pengadilan, tanggal, terdakwa, jenis,
                berat, pasalDakwaan, pasalTerbukti, tuntutan, vonis, denda, "-", "-", "-", "-");
        service.tambahPutusan(baru);
        System.out.println("[OK] Putusan " + caseId + " berhasil ditambahkan.");
    }

    private static void menuHapusPutusan() {
        int idx = Integer.parseInt(bacaInput("Masukkan indeks putusan yang akan dihapus (0 - "
                + (service.getDaftarPutusan().size() - 1) + "): "));
        Putusan dihapus = service.hapusPutusan(idx);
        System.out.println("[OK] Berhasil menghapus: " + dihapus);
    }

    private static void menuCariPutusan() {
        String mode = bacaInput("Cari berdasarkan (1) caseId atau (2) nomorPerkara? ");
        Putusan ditemukan;
        if ("1".equals(mode)) {
            String id = bacaInput("Masukkan caseId (misal PTS-001): ");
            ditemukan = service.cariByCaseId(id);
        } else {
            String no = bacaInput("Masukkan kata kunci nomorPerkara: ");
            ditemukan = service.cariByNomorPerkara(no);
        }

        System.out.println("Jumlah langkah pencarian: " + service.getLastSearchSteps() + " langkah");
        if (ditemukan != null) {
            System.out.println("[DITEMUKAN] " + ditemukan);
            System.out.println("  Pasal Terbukti : " + ditemukan.getPasalTerbukti());
            System.out.println("  Barang Bukti   : " + ditemukan.getBarangBukti());
            System.out.println("  Sumber Halaman : " + ditemukan.getSourcePages());
            DynamicArray<Putusan> single = new DynamicArray<>(1);
            single.add(ditemukan);
            hasilTerakhir = single;
        } else {
            System.out.println("[INFO] Data putusan tidak ditemukan.");
        }
    }

    private static void menuFilterPutusan() {
        System.out.println("\n--- FILTER GABUNGAN (Tekan Enter untuk melewati kriteria) ---");
        String jenis = bacaInput("Jenis Narkotika (misal Shabu): ");
        String minBStr = bacaInput("Berat Minimum (gram): ");
        String maxBStr = bacaInput("Berat Maksimum (gram): ");
        String pasal = bacaInput("Pasal Terbukti (misal 112): ");
        String minVStr = bacaInput("Vonis Minimum (bulan): ");
        String maxVStr = bacaInput("Vonis Maksimum (bulan): ");

        double minB = minBStr.isEmpty() ? -1 : Double.parseDouble(minBStr);
        double maxB = maxBStr.isEmpty() ? -1 : Double.parseDouble(maxBStr);
        int minV = minVStr.isEmpty() ? -1 : Integer.parseInt(minVStr);
        int maxV = maxVStr.isEmpty() ? -1 : Integer.parseInt(maxVStr);

        DynamicArray<Putusan> hasil = service.filterPutusan(jenis, minB, maxB, pasal, minV, maxV);
        hasilTerakhir = hasil;
        System.out.println("\nDitemukan " + hasil.size() + " putusan yang cocok:");
        tampilkanTabelPutusan(hasil);
    }

    private static void menuUrutkanPutusan() {
        String arah = bacaInput("Urutkan vonis (1) Terpendek ke Terpanjang [ASC] atau (2) Terpanjang ke Terpendek [DESC]? ");
        boolean asc = !"2".equals(arah);
        DynamicArray<Putusan> terurut = service.urutkanByVonis(asc);
        hasilTerakhir = terurut;
        tampilkanTabelPutusan(terurut);
    }

    private static void menuLihatAudit() {
        ArrayStack<String> log = service.getAuditLog();
        System.out.println("\n=== RIWAYAT AUDIT OPERASI (TERBARU DI ATAS / LIFO) ===");
        if (log.isEmpty()) {
            System.out.println("(Belum ada riwayat aktivitas)");
            return;
        }
        for (int i = 0; i < log.size(); i++) {
            System.out.println((i + 1) + ". " + log.getFromTop(i));
        }
    }

    private static void menuEksporData() throws IOException {
        String path = bacaInput("Nama file output (Enter untuk 'docs/demo/hasil-ekspor.csv'): ");
        if (path.isEmpty()) path = "docs/demo/hasil-ekspor.csv";
        service.eksporKeCsv(hasilTerakhir, path);
        System.out.println("[OK] Berhasil mengekspor " + hasilTerakhir.size() + " baris ke " + path);
    }

    private static String bacaInput(String prompt) {
        System.out.print(prompt);
        try {
            String line = reader.readLine();
            return (line == null) ? "" : line.trim();
        } catch (IOException e) {
            return "";
        }
    }

    private static String potongTeks(String teks, int maxLen) {
        if (teks == null) return "";
        if (teks.length() <= maxLen) return teks;
        return teks.substring(0, maxLen - 3) + "...";
    }
}