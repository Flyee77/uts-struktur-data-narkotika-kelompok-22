# Dokumentasi Penggunaan AI (AI Usage Log)

Dokumen ini mencatat pemanfaatan asisten AI selama proses perancangan, pengembangan, pengujian, dan dokumentasi proyek **Sistem Pengelolaan Data Putusan Narkotika** oleh Kelompok 22 (**Ahmad Azzam Saputra** & **Muhammad Arjun Amirul Wibowo**).

## Ringkasan Penggunaan
| Tahap Pengerjaan | Peran AI | Verifikasi & Modifikasi oleh Kelompok |
| :--- | :--- | :--- |
| **Perancangan Struktur Data** | Membantu menyusun kerangka awal generic `DynamicArray<T>` dan `ArrayStack<T>` berbasis array statis Java. | Kelompok memastikan nol penggunaan koleksi `java.util`, menambahkan method `validateInvariant()`, dan menyesuaikan pengecekan batas indeks. |
| **Parsing Dataset CSV** | Membantu membuat logika pemisahan kolom CSV yang mengandung tanda kutip ganda (`"..."`). | Kelompok menyesuaikan pembersihan format angka desimal bertitik ganda (pada `beratBarangBuktiGram`) dan nominal rupiah dari data `data/putusan-50.csv`. |
| **Logika Fitur & CLI** | Membantu merancang kerangka menu interaktif berbasis `BufferedReader` dan algoritma *Insertion Sort* non-destruktif. | Kelompok menguji seluruh 9 fitur pada dataset 50 putusan dan memverifikasi pencatatan log aktivitas ke `ArrayStack`. |
| **Pengujian & Alur Git** | Membantu menyusun 7 skenario unit test JUnit 5 dan panduan alur *branching* / Pull Request kolaboratif. | Seluruh unit test dieksekusi secara lokal (`mvn test`) hingga lulus 100% dan setiap PR di-review secara silang oleh kedua anggota. |

## Pernyataan Tanggung Jawab
Seluruh kode sumber dan dokumentasi yang masuk ke branch `main` telah dipahami, diuji coba langsung dengan dataset kelompok (`data/putusan-50.csv`), dan diverifikasi melalui mekanisme *Pull Request* serta *Code Review* oleh masing-masing anggota kelompok.
