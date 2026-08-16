# FixIt Application

FixIt adalah aplikasi Android berbasis Jetpack Compose yang dirancang untuk memudahkan pelaporan kerusakan fasilitas di lingkungan kampus atau organisasi. Aplikasi ini memungkinkan pengguna (mahasiswa/staf) untuk melaporkan kerusakan secara real-time dan memungkinkan admin untuk memantau serta memperbarui status perbaikan.

## 🚀 Fitur Utama

### Pengguna (Pelapor)
- **Autentikasi**: Login, Register, dan Lupa Password menggunakan Firebase Authentication.
- **Login Google**: Integrasi Google Sign-In untuk akses cepat.
- **Lapor Kerusakan**: Formulir pelaporan dengan judul, deskripsi, lokasi, dan unggah foto (kamera/galeri).
- **Dashboard Home**: Melihat daftar aduan yang telah dikirim beserta status terbarunya (Pending, In Progress, Completed).
- **Profil**: Informasi pengguna dan fitur logout.

### Admin (Dashboard)
- **Ringkasan Aduan**: Melihat semua aduan yang masuk dari seluruh pengguna.
- **Manajemen Status**: Memperbarui status kerusakan untuk memberikan informasi terbaru kepada pelapor.

## 🛠️ Teknologi yang Digunakan

- **Bahasa**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Design System**: Material Design 3 (M3)
- **Backend**: [Firebase](https://firebase.google.com/)
    - Firebase Auth (Authentication)
    - Firestore (Database NoSQL)
    - Firebase Storage (Penyimpanan Gambar)
- **Library Pihak Ketiga**:
    - [Coil](https://coil-kt.github.io/coil/): Image loading
    - [ViewModel & LiveData](https://developer.android.com/topic/libraries/architecture/viewmodel): State management
    - [Google Play Services Auth](https://developers.google.com/identity/sign-in/android/start-integrating): Google Sign-in

## 🎨 Tema Aplikasi

Aplikasi ini menggunakan tema kustom dengan palet warna:
- **Background**: Putih bersih untuk keterbacaan tinggi.
- **Cards & Primary Elements**: Biru (`BluePrimary`) untuk memberikan kesan profesional dan terpercaya.
- **Typography**: Menggunakan font Material 3 yang modern dan responsif.

## 📸 Tampilan Layar

1. **Splash Screen**: Animasi awal aplikasi.
2. **Login/Register**: Antarmuka masuk pengguna.
3. **Home Screen**: Daftar aduan dengan indikator status berwarna.
4. **Report Form**: Antarmuka pengambilan gambar dan pengisian detail kerusakan.
5. **Admin Dashboard**: Panel kontrol untuk pengelolaan aduan.

## ⚙️ Cara Menjalankan Project

1. Clone repository ini.
2. Buka di **Android Studio (Ladybug atau lebih baru)**.
3. Hubungkan project dengan Firebase Console Anda sendiri.
4. Unduh file `google-services.json` dan letakkan di folder `app/`.
5. Pastikan plugin Google Services sudah terkonfigurasi di `build.gradle`.
6. Run aplikasi pada Emulator atau Perangkat Fisik.

---
Dikembangkan dengan ❤️ untuk kemudahan pelaporan fasilitas.
