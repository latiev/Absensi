package id.latiev.absensi.model;

/**
 * Created by Latiev on 7/20/2016.
 */
public class Kegiatan {

    private String jam;
    private String namaKegiatan;
    private String keterangan;
    private int gambarAtas;
    private int gambarBawah;

    public Kegiatan(){

    }

    public Kegiatan(String jam, String namaKegiatan, String keterangan) {
        this.jam = jam;
        this.namaKegiatan = namaKegiatan;
        this.keterangan = keterangan;
    }

    public Kegiatan(String jam, String namaKegiatan, String keterangan, int gambarAtas, int gambarBawah) {
        this.jam = jam;
        this.namaKegiatan = namaKegiatan;
        this.keterangan = keterangan;
        this.gambarAtas = gambarAtas;
        this.gambarBawah = gambarBawah;
    }

    public int getGambarAtas() {
        return gambarAtas;
    }

    public void setGambarAtas(int gambarAtas) {
        this.gambarAtas = gambarAtas;
    }

    public int getGambarBawah() {
        return gambarBawah;
    }

    public void setGambarBawah(int gambarBawah) {
        this.gambarBawah = gambarBawah;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getNamaKegiatan() {
        return namaKegiatan;
    }

    public void setNamaKegiatan(String namaKegiatan) {
        this.namaKegiatan = namaKegiatan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
