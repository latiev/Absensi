package id.latiev.absensi.model;

/**
 * Created by Latiev on 7/22/2016.
 */
public class Presensi {

    String id, tanggal, idUser, masuk, pulang;

    public Presensi(){

    }

    public Presensi(String id, String tanggal, String idUser, String masuk, String pulang) {
        this.id = id;
        this.tanggal = tanggal;
        this.idUser = idUser;
        this.masuk = masuk;
        this.pulang = pulang;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getMasuk() {
        return masuk;
    }

    public void setMasuk(String masuk) {
        this.masuk = masuk;
    }

    public String getPulang() {
        return pulang;
    }

    public void setPulang(String pulang) {
        this.pulang = pulang;
    }
}
