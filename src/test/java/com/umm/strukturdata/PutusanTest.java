package com.umm.strukturdata;

import com.umm.strukturdata.datastructure.ArrayStack;
import com.umm.strukturdata.datastructure.DynamicArray;
import com.umm.strukturdata.model.Putusan;
import com.umm.strukturdata.service.PutusanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PutusanTest {

    private PutusanService service;

    @BeforeEach
    void setUp() throws IOException {
        service = new PutusanService();
        service.loadFromCsv("data/putusan-50.csv");
    }

    @Test
    void skenario1_muat50DatasetCsvDanValidasiStruktur() {
        DynamicArray<Putusan> daftar = service.getDaftarPutusan();
        assertEquals(50, daftar.size(), "Jumlah dataset yang dimuat harus 50");
        assertTrue(daftar.validateInvariant(), "Invariant DynamicArray harus valid setelah load");
        assertTrue(service.getAuditLog().validateInvariant(), "Invariant ArrayStack harus valid");
        assertNotNull(daftar.get(0).getCaseId());
        assertNotNull(daftar.get(49).getCaseId());
    }

    @Test
    void skenario2_tambahDanHapusPutusan() {
        Putusan baru = new Putusan(
                "PTS-051", "999/Pid.Sus/2026/PN Mlg", "Pengadilan Negeri Malang",
                "01/11/2026", "TDW-051", "Metamfetamina (Shabu)", 1.50,
                "Pasal 112 ayat (1) UU No. 35 Tahun 2009",
                "Pasal 112 ayat (1) UU No. 35 Tahun 2009",
                60, 48, 800000000L, "1 klip shabu", "-", "-", "hlm. 1-10"
        );

        service.tambahPutusan(baru);
        assertEquals(51, service.getDaftarPutusan().size());
        assertTrue(service.getDaftarPutusan().validateInvariant());

        Putusan dihapus = service.hapusPutusan(50);
        assertEquals("PTS-051", dihapus.getCaseId());
        assertEquals(50, service.getDaftarPutusan().size());
        assertTrue(service.getDaftarPutusan().validateInvariant());
    }

    @Test
    void skenario3_tolakDuplikatDanIndeksTidakValid() {
        Putusan pertama = service.getDaftarPutusan().get(0);
        assertThrows(IllegalArgumentException.class, () -> service.tambahPutusan(pertama));
        assertThrows(IndexOutOfBoundsException.class, () -> service.getDaftarPutusan().get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> service.hapusPutusan(99));
    }

    @Test
    void skenario4_pencarianDenganHitungLangkah() {
        Putusan ketemu = service.cariByCaseId("PTS-001");
        assertNotNull(ketemu);
        assertEquals(1, service.getLastSearchSteps(), "PTS-001 berada di indeks 0 sehingga butuh 1 langkah");

        Putusan tidakKetemu = service.cariByCaseId("PTS-999");
        assertNull(tidakKetemu);
        assertEquals(50, service.getLastSearchSteps(), "Pencarian gagal harus memeriksa seluruh 50 elemen");
    }

    @Test
    void skenario5_filterGabungan() {
        DynamicArray<Putusan> hasil = service.filterPutusan("Shabu", 0.0, -1, "", 12, -1);
        assertTrue(hasil.validateInvariant());
        for (int i = 0; i < hasil.size(); i++) {
            Putusan p = hasil.get(i);
            assertTrue(p.getJenisNarkotika().toLowerCase().contains("shabu"));
            assertTrue(p.getVonisBulan() >= 12);
        }
    }

    @Test
    void skenario6_pengurutanNonDestruktif() {
        String idPertamaSebelumSort = service.getDaftarPutusan().get(0).getCaseId();

        DynamicArray<Putusan> terurutAsc = service.urutkanByVonis(true);
        assertEquals(50, terurutAsc.size());
        for (int i = 0; i < terurutAsc.size() - 1; i++) {
            assertTrue(terurutAsc.get(i).getVonisBulan() <= terurutAsc.get(i + 1).getVonisBulan());
        }

        // Pastikan urutan pada struktur utama tidak berubah
        assertEquals(idPertamaSebelumSort, service.getDaftarPutusan().get(0).getCaseId());
    }

    @Test
    void skenario7_riwayatAuditStackLifo() {
        ArrayStack<String> log = service.getAuditLog();
        int ukuranAwal = log.size();

        service.cariByCaseId("PTS-010");
        assertEquals(ukuranAwal + 1, log.size());
        assertTrue(log.peek().contains("PTS-010"), "Elemen paling atas stack harus operasi terakhir");
    }
}