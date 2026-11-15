# Sistem Pengelolaan Data Putusan Narkotika (Custom Data Structures)

Proyek Ujian Tengah Semester (UTS) Mata Kuliah Struktur Data — Program Studi Informatika, Universitas Muhammadiyah Malang (UMM). Aplikasi berbasis Java CLI ini mengelola dataset 50 putusan pengadilan perkara narkotika menggunakan implementasi struktur data kustom dari nol tanpa koleksi bawaan `java.util`.

## Anggota Kelompok 22
| Nama Lengkap | NIM | Akun GitHub | Peran Utama |
| :--- | :--- | :--- | :--- |
| **Ahmad Azzam Saputra** | `202510370110119` | [@Flyee77](https://github.com/Flyee77) | Struktur Data Kustom (`DynamicArray`, `ArrayStack`), Model `Putusan`, `CsvParser`, Unit Test JUnit 5, & Data Provenance |
| **Muhammad Arjun Amirul Wibowo** | `202510370110133` | [@muhamadarjun-de](https://github.com/muhamadarjun-de) | Implementasi 9 Fitur (`PutusanService`), Antarmuka CLI (`MainApp`), Dokumentasi (`README.md`, `AI_USAGE.md`), & Data Provenance |

## Arsitektur & Struktur Data Kustom
Seluruh struktur penyimpanan dan riwayat operasi dibangun secara mandiri di dalam package `com.umm.strukturdata.datastructure` tanpa `ArrayList`, `LinkedList`, `Stack`, maupun `HashMap`:
1. **`DynamicArray<T>` (Struktur Utama):** Menyimpan objek `Putusan` dengan ekspansi kapasitas otomatis ($2\times$ kapasitas saat penuh) dan pengecekan konsistensi internal melalui `validateInvariant()`.
2. **`ArrayStack<T>` (Struktur Sekunder):** Menerapkan prinsip *Last-In, First-Out* (LIFO) untuk mencatat jejak audit setiap operasi pengguna (*load*, *add*, *remove*, *search*, *filter*, *sort*, *export*).

## Daftar Fitur Utama
1. **Muat Dataset CSV:** Membaca 50 data dari `data/putusan-50.csv` dengan penanganan koma di dalam tanda kutip ganda dan normalisasi format angka/rupiah.
2. **Tampilkan Ringkasan Putusan:** Menampilkan tabel ringkasan seluruh perkara yang tersimpan di `DynamicArray`.
3. **Tambah & Hapus Putusan:** Menambah data baru dengan validasi anti-duplikat `caseId` serta menghapus data berdasarkan indeks.
4. **Pencarian & Pelacakan Langkah:** Mencari berdasarkan `caseId` atau `nomorPerkara` sekaligus menampilkan jumlah langkah iterasi pencarian.
5. **Filter Gabungan:** Menyaring putusan berdasarkan kombinasi jenis narkotika, rentang berat barang bukti (gram), pasal terbukti, dan rentang lama vonis (bulan).
6. **Agregasi Statistik:** Menghitung total perkara, rata-rata/min/max vonis, total berat barang bukti, total denda, serta distribusi jenis narkotika menggunakan array paralel.
7. **Pengurutan Non-Destruktif:** Mengurutkan putusan berdasarkan lama vonis (ASC/DESC) dengan *Insertion Sort* pada salinan baru tanpa mengubah urutan struktur utama.
8. **Riwayat Audit (LIFO):** Menampilkan log aktivitas terbaru di posisi teratas menggunakan `ArrayStack`.
9. **Ekspor CSV:** Menyimpan hasil daftar/filter/urut terakhir ke dalam file CSV baru (`docs/demo/hasil-ekspor.csv`).

## Cara Kompilasi, Menjalankan Aplikasi, dan Unit Test
Pastikan **JDK 21** dan **Apache Maven 3.9+** sudah terpasang di sistem:

```bash
# 1. Kompilasi proyek
mvn compile

# 2. Jalankan aplikasi CLI
java -cp target/classes com.umm.strukturdata.app.MainApp

# 3. Jalankan 7 skenario pengujian otomatis (JUnit 5)
mvn test

---

### 2. Isi File `AI_USAGE.md` (Simpan di folder utama proyek)

```markdown
# Dokumentasi Penggunaan AI (AI Usage Log)

Dokumen ini mencatat pemanfaatan asisten AI selama proses perancangan, pengembangan, pengujian, dan dokumentasi proyek **Sistem Pengelolaan Data Putusan Narkotika**.

## Ringkasan Penggunaan
| Tahap Pengerjaan | Peran AI | Verifikasi & Modifikasi oleh Kelompok |
| :--- | :--- | :--- |
| **Perancangan Struktur Data** | Membantu menyusun kerangka awal generic `DynamicArray<T>` dan `ArrayStack<T>` berbasis array statis Java. | Kelompok memastikan nol penggunaan koleksi `java.util`, menambahkan method `validateInvariant()`, dan menyesuaikan pengecekan batas indeks. |
| **Parsing Dataset CSV** | Membantu membuat logika pemisahan kolom CSV yang mengandung tanda kutip ganda (`"..."`). | Kelompok menyesuaikan pembersihan format angka desimal bertitik ganda (contoh pada `beratBarangBuktiGram`) dan nominal rupiah dari data `data/putusan-50.csv`. |
| **Logika Fitur & CLI** | Membantu merancang kerangka menu interaktif berbasis `BufferedReader` dan algoritma *Insertion Sort* non-destruktif. | Kelompok menguji seluruh 9 fitur pada dataset 50 putusan dan memverifikasi pencatatan log ke `ArrayStack`. |
| **Pengujian & Alur Git** | Membantu menyusun 7 skenario unit test JUnit 5 dan panduan alur *branching* / Pull Request kolaboratif. | Seluruh unit test dieksekusi secara lokal (`mvn test`) hingga lulus 100% dan setiap PR di-review secara silang oleh kedua anggota. |

## Pernyataan Tanggung Jawab
Seluruh kode sumber yang masuk ke branch `main` telah dipahami, diuji coba langsung dengan dataset kelompok (`data/putusan-50.csv`), dan diverifikasi melalui mekanisme *Pull Request* serta *Code Review* oleh masing-masing anggota kelompok.
