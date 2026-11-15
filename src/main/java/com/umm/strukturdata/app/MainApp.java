package com.umm.strukturdata.app;

import com.umm.strukturdata.datastructure.ArrayStack;
import com.umm.strukturdata.datastructure.DynamicArray;
import com.umm.strukturdata.model.Putusan;
import com.umm.strukturdata.service.PutusanService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainApp {
    public static void main(String[] args) {
        PutusanService service = new PutusanService();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            service.loadFromCsv("data/putusan-50.csv");
            System.out.println("Berhasil memuat awal " + service.getDaftarPutusan().size() + " data putusan.");
        } catch (IOException e) {
            System.out.println("Peringatan: Gagal memuat awal CSV (" + e.getMessage() + ")");
        }

        boolean running = true;
        while (running) {
            System.out.println("\n=======================================================");
            System.out.println(" SISTEM PENGELOLAAN PUTUSAN NARKOTIKA (CUSTOM DS)");
            System.out.println("=======================================================");
            System.out.println("1. Muat Ulang Dataset CSV (data/putusan-50.csv)");
            System.out.println("2. Tampilkan Seluruh Putusan");
            System.out.println("3. Tambah Putusan Baru");
            System.out.println("4. Hapus Putusan Berdasarkan Indeks");
            System.out.println("5. Cari Putusan (Case ID / Nomor Perkara)");
            System.out.println("6. Filter Gabungan Putusan");
            System.out.println("7. Tampilkan Agregasi Statistik");
            System.out.println("8. Urutkan Putusan Berdasarkan Vonis (Non-Destruktif)");
            System.out.println("9. Lihat Riwayat Aktivitas (ArrayStack LIFO)");
            System.out.println("10. Ekspor Hasil Terakhir ke CSV");
            System.out.println("0. Keluar");
            System.out.print("Pilih menu [0-10]: ");

            try {
                String pilihan = reader.readLine();
                if (pilihan == null) break;

                switch (pilihan.trim()) {
                    case "1":
                        service.loadFromCsv("data/putusan-50.csv");
                        System.out.println("Dataset berhasil dimuat: " + service.getDaftarPutusan().size() + " putusan.");
                        break;
                    case "2":
                        tampilkanDaftar(service.getDaftarPutusan());
                        break;
                    case "3":
                        System.out.print("Case ID (misal PTS-051): ");
                        String id = reader.readLine();
                        System.out.print("Nomor Perkara: ");
                        String no = reader.readLine();
                        System.out.print("Pengadilan: ");
                        String pn = reader.readLine();
                        System.out.print("Tanggal Putusan (DD/MM/YYYY): ");
                        String tgl = reader.readLine();
                        System.out.print("Kode Terdakwa: ");
                        String tdw = reader.readLine();
                        System.out.print("Jenis Narkotika: ");
                        String jn = reader.readLine();
                        System.out.print("Berat Barang Bukti (gram): ");
                        double berat = Double.parseDouble(reader.readLine().trim());
                        System.out.print("Pasal Dakwaan: ");
                        String dakwaan = reader.readLine();
                        System.out.print("Pasal Terbukti: ");
                        String terbukti = reader.readLine();
                        System.out.print("Tuntutan (bulan): ");
                        int tuntutan = Integer.parseInt(reader.readLine().trim());
                        System.out.print("Vonis (bulan): ");
                        int vonis = Integer.parseInt(reader.readLine().trim());
                        System.out.print("Denda (Rupiah): ");
                        long denda = Long.parseLong(reader.readLine().trim());

                        Putusan baru = new Putusan(id, no, pn, tgl, tdw, jn, berat, dakwaan, terbukti,
                                tuntutan, vonis, denda, "Barang bukti disita", "-", "-", "hlm. 1-10");
                        service.tambahPutusan(baru);
                        System.out.println("Putusan berhasil ditambahkan!");
                        break;
                    case "4":
                        System.out.print("Masukkan indeks (0 s.d. " + (service.getDaftarPutusan().size() - 1) + "): ");
                        int idx = Integer.parseInt(reader.readLine().trim());
                        Putusan dihapus = service.hapusPutusan(idx);
                        System.out.println("Berhasil menghapus: " + dihapus);
                        break;
                    case "5":
                        System.out.print("Cari berdasarkan (1) Case ID atau (2) Nomor Perkara? ");
                        String mode = reader.readLine().trim();
                        System.out.print("Masukkan kata kunci: ");
                        String key = reader.readLine();
                        Putusan hasilCari = "1".equals(mode)
                                ? service.cariByCaseId(key)
                                : service.cariByNomorPerkara(key);
                        if (hasilCari != null) {
                            System.out.println("Ditemukan dalam " + service.getLastSearchSteps() + " langkah:");
                            System.out.println(hasilCari);
                        } else {
                            System.out.println("Data tidak ditemukan setelah memeriksa " + service.getLastSearchSteps() + " elemen.");
                        }
                        break;
                    case "6":
                        System.out.print("Jenis Narkotika (kosongkan jika semua): ");
                        String fJenis = reader.readLine();
                        System.out.print("Min Berat gram (-1 jika abaikan): ");
                        double fMinB = Double.parseDouble(reader.readLine().trim());
                        System.out.print("Max Berat gram (-1 jika abaikan): ");
                        double fMaxB = Double.parseDouble(reader.readLine().trim());
                        System.out.print("Pasal Terbukti (kosongkan jika semua): ");
                        String fPasal = reader.readLine();
                        System.out.print("Min Vonis bulan (-1 jika abaikan): ");
                        int fMinV = Integer.parseInt(reader.readLine().trim());
                        System.out.print("Max Vonis bulan (-1 jika abaikan): ");
                        int fMaxV = Integer.parseInt(reader.readLine().trim());

                        DynamicArray<Putusan> hasilFilter = service.filterPutusan(fJenis, fMinB, fMaxB, fPasal, fMinV, fMaxV);
                        tampilkanDaftar(hasilFilter);
                        break;
                    case "7":
                        System.out.println(service.getRingkasanStatistik());
                        break;
                    case "8":
                        System.out.print("Urutkan (1) Ascending / Terpendek atau (2) Descending / Terlama? ");
                        boolean asc = "1".equals(reader.readLine().trim());
                        DynamicArray<Putusan> terurut = service.urutkanByVonis(asc);
                        tampilkanDaftar(terurut);
                        break;
                    case "9":
                        ArrayStack<String> stack = service.getAuditLog();
                        System.out.println("=== RIWAYAT AKTIVITAS (TERBARU DI ATAS) ===");
                        for (int i = 0; i < stack.size(); i++) {
                            System.out.println((i + 1) + ". " + stack.getFromTop(i));
                        }
                        break;
                    case "10":
                        String out = "docs/demo/hasil-ekspor.csv";
                        service.exportToCsv(out, service.getLastResult());
                        System.out.println("Berhasil mengekspor " + service.getLastResult().size() + " data ke " + out);
                        break;
                    case "0":
                        running = false;
                        System.out.println("Keluar dari aplikasi.");
                        break;
                    default:
                        System.out.println("Pilihan tidak valid.");
                }
            } catch (Exception e) {
                System.out.println("Terjadi kesalahan input/operasi: " + e.getMessage());
            }
        }
    }

    private static void tampilkanDaftar(DynamicArray<Putusan> daftar) {
        System.out.println("\nMenampilkan " + daftar.size() + " data putusan:");
        for (int i = 0; i < daftar.size(); i++) {
            System.out.printf("%2d. %s%n", i, daftar.get(i));
        }
    }
}
