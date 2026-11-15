# Laporan Kontribusi Kelompok 22

**Mata Kuliah:** Struktur Data  
**Program Studi:** Informatika — Universitas Muhammadiyah Malang (UMM)  
**Repositori:** [https://github.com/Flyee77/uts-struktur-data-narkotika-kelompok-22](https://github.com/Flyee77/uts-struktur-data-narkotika-kelompok-22)

---

## 1. Identitas Anggota
1. **Ahmad Azzam Saputra** — NIM `202510370110119` (GitHub: `Flyee77`)
2. **Muhammad Arjun Amirul Wibowo** — NIM `202510370110133` (GitHub: `muhamadarjun-de`)

---

## 2. Pembagian Tugas & Ekstraksi Dataset (50 Putusan)
* **Ekstraksi & Verifikasi Data (`data/putusan-50.csv` & `docs/data-provenance.csv`):**
  * **Ahmad Azzam Saputra (`Flyee77`):** Mengekstrak 25 putusan bernomor ganjil (`PTS-001`, `PTS-003`, ..., `PTS-049`) dan memverifikasi silang (*review*) 25 putusan bernomor genap.
  * **Muhammad Arjun Amirul Wibowo (`muhamadarjun-de`):** Mengekstrak 25 putusan bernomor genap (`PTS-002`, `PTS-004`, ..., `PTS-050`) dan memverifikasi silang (*review*) 25 putusan bernomor ganjil.
* **Pengembangan Kode & Pengujian:**
  * **Ahmad Azzam Saputra (`Flyee77`):** Konfigurasi Maven (`pom.xml`), kontrak interface `StrukturData`, implementasi `DynamicArray<T>` dan `ArrayStack<T>` tanpa `java.util`, entitas `Putusan`, `CsvParser`, 7 skenario pengujian JUnit 5 (`PutusanTest.java`), serta dokumentasi `AI_USAGE.md`.
  * **Muhammad Arjun Amirul Wibowo (`muhamadarjun-de`):** Implementasi 9 fitur fungsional pada `PutusanService.java`, antarmuka CLI `MainApp.java`, pembaruan `README.md`, serta penyusunan `CONTRIBUTIONS.md` dan ADR.

---

## 3. Rekam Jejak Kolaborasi GitHub (Pull Request & Code Review)

| No. PR | Pembuat PR (Author) | Reviewer & Approver | Ruang Lingkup Perubahan | Tautan Pull Request |
| :--- | :--- | :--- | :--- | :--- |
| **PR #2** | Ahmad Azzam Saputra (`Flyee77`) | Muhammad Arjun Amirul Wibowo (`muhamadarjun-de`) | Struktur data `DynamicArray`, `ArrayStack`, `Putusan`, `CsvParser`, `pom.xml`, & `putusan-50.csv` | https://github.com/Flyee77/uts-struktur-data-narkotika-kelompok-22/pull/2 |
| **PR #3** | Ahmad Azzam Saputra (`Flyee77`) | Muhammad Arjun Amirul Wibowo (`muhamadarjun-de`) | Penambahan unit test JUnit 5 (`PutusanTest.java`) | https://github.com/Flyee77/uts-struktur-data-narkotika-kelompok-22/pull/3 |
| **PR #4** | Ahmad Azzam Saputra (`Flyee77`) | Muhammad Arjun Amirul Wibowo (`muhamadarjun-de`) | Struktur folder `docs/` dan `docs/data-provenance.csv` awal | https://github.com/Flyee77/uts-struktur-data-narkotika-kelompok-22/pull/4 |
| **PR #5** | Ahmad Azzam Saputra (`Flyee77`) | Muhammad Arjun Amirul Wibowo (`muhamadarjun-de`) | Kelengkapan 50 baris `docs/data-provenance.csv` dan bukti ekspor `docs/demo/hasil-ekspor.csv` | https://github.com/Flyee77/uts-struktur-data-narkotika-kelompok-22/pull/5 |
| **PR #6** | Muhammad Arjun Amirul Wibowo (`muhamadarjun-de`) | Ahmad Azzam Saputra (`Flyee77`) | Implementasi `PutusanService.java` (9 fitur), `MainApp.java` (CLI), dan pembaruan `README.md` | https://github.com/Flyee77/uts-struktur-data-narkotika-kelompok-22/pull/6 |
| **PR #7** | Ahmad Azzam Saputra (`Flyee77`) | Muhammad Arjun Amirul Wibowo (`muhamadarjun-de`) | Penyusunan `AI_USAGE.md` | https://github.com/Flyee77/uts-struktur-data-narkotika-kelompok-22/pull/7 |
| **PR #8** | Muhammad Arjun Amirul Wibowo (`muhamadarjun-de`) | Ahmad Azzam Saputra (`Flyee77`) | Finalisasi `CONTRIBUTIONS.md` | https://github.com/Flyee77/uts-struktur-data-narkotika-kelompok-22/pull/8 |
| **PR #9** | Muhammad Arjun Amirul Wibowo (`muhamadarjun-de`) | Ahmad Azzam Saputra (`Flyee77`) | Dokumentasi keputusan arsitektur struktur data (`docs/adr/001-struktur-data.md`) | https://github.com/Flyee77/uts-struktur-data-narkotika-kelompok-22/pull/9 |

---

## 4. Ringkasan Statistik Kontribusi
* **Ahmad Azzam Saputra (`Flyee77`):** 5 Pull Request ter-merge (`#2`, `#3`, `#4`, `#5`, `#7`) & 3 Code Review (*Approved* pada `#6`, `#8`, `#9`).
* **Muhammad Arjun Amirul Wibowo (`muhamadarjun-de`):** 3 Pull Request ter-merge (`#6`, `#8`, `#9`) & 5 Code Review (*Approved* pada `#2`, `#3`, `#4`, `#5`, `#7`).
