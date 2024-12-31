import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    // Oyun tahtası
    int kareBoyutu = 32;
    int satirlar = 16;
    int sutunlar = 16;

    int tahtaGenisligi = kareBoyutu * sutunlar; // 32 * 16
    int tahtaYuksekligi = kareBoyutu * satirlar; // 32 * 16

    Image gemiResim;
    Image dusmanResim;
    Image dusmanCyanResim;
    Image dusmanMagentaResim;
    Image dusmanSariResim;
    ArrayList<Image> dusmanResimListesi;

    class Blok {
        int x;
        int y;
        int genislik;
        int yukseklik;
        Image resim;
        boolean yasiyor = true; // Dusmanlar icin kullaniliyor
        boolean kullanildi = false; // Mermiler icin kullaniliyor

        Blok(int x, int y, int genislik, int yukseklik, Image resim) {
            this.x = x;
            this.y = y;
            this.genislik = genislik;
            this.yukseklik = yukseklik;
            this.resim = resim;
        }
    }

    // Gemi
    int gemiGenislik = kareBoyutu * 2;
    int gemiYukseklik = kareBoyutu;
    int gemiX = kareBoyutu * sutunlar / 2 - kareBoyutu;
    int gemiY = kareBoyutu * satirlar - kareBoyutu * 2;
    int gemiHizX = kareBoyutu; // Gemi hareket hızı
    Blok gemi;

    // Dusmanlar
    ArrayList<Blok> dusmanListesi;
    int dusmanGenislik = kareBoyutu * 2;
    int dusmanYukseklik = kareBoyutu;
    int dusmanX = kareBoyutu;
    int dusmanY = kareBoyutu;

    int dusmanSatirlari = 2;
    int dusmanSutunlari = 3;
    int dusmanSayisi = 0; // Yenilecek dusman sayisi
    int dusmanHizX = 1; // Dusman hareket hızı

    // Mermiler
    ArrayList<Blok> mermiListesi;
    int mermiGenislik = kareBoyutu / 8;
    int mermiYukseklik = kareBoyutu / 2;
    int mermiHizY = -10; // Mermi hareket hızı

    Timer oyunDongusu;
    boolean oyunBitti = false;
    int puan = 0;

    SpaceInvaders() {
        setPreferredSize(new Dimension(tahtaGenisligi, tahtaYuksekligi));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        // Resimleri yükle
        gemiResim = new ImageIcon(getClass().getResource("./ship.png")).getImage();
        dusmanResim = new ImageIcon(getClass().getResource("./alien.png")).getImage();
        dusmanCyanResim = new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        dusmanMagentaResim = new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();
        dusmanSariResim = new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();

        dusmanResimListesi = new ArrayList<Image>();
        dusmanResimListesi.add(dusmanResim);
        dusmanResimListesi.add(dusmanCyanResim);
        dusmanResimListesi.add(dusmanMagentaResim);
        dusmanResimListesi.add(dusmanSariResim);

        gemi = new Blok(gemiX, gemiY, gemiGenislik, gemiYukseklik, gemiResim);
        dusmanListesi = new ArrayList<Blok>();
        mermiListesi = new ArrayList<Blok>();

        // Oyun zamanlayıcısı
        oyunDongusu = new Timer(1000 / 60, this); // 1000/60 = 16.6 ms
        dusmanlariOlustur();
        oyunDongusu.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ciz(g);
    }

    public void ciz(Graphics g) {
        // Gemi
        g.drawImage(gemi.resim, gemi.x, gemi.y, gemi.genislik, gemi.yukseklik, null);

        // Dusmanlar
        for (int i = 0; i < dusmanListesi.size(); i++) {
            Blok dusman = dusmanListesi.get(i);
            if (dusman.yasiyor) {
                g.drawImage(dusman.resim, dusman.x, dusman.y, dusman.genislik, dusman.yukseklik, null);
            }
        }

        // Mermiler
        g.setColor(Color.white);
        for (int i = 0; i < mermiListesi.size(); i++) {
            Blok mermi = mermiListesi.get(i);
            if (!mermi.kullanildi) {
                g.drawRect(mermi.x, mermi.y, mermi.genislik, mermi.yukseklik);
            }
        }

        // Puan
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (oyunBitti) {
            g.drawString("Oyun Bitti: " + String.valueOf((int) puan), 10, 35);
        } else {
            g.drawString(String.valueOf((int) puan), 10, 35);
        }
    }

    public void hareketEttir() {
        // Dusmanlar
        for (int i = 0; i < dusmanListesi.size(); i++) {
            Blok dusman = dusmanListesi.get(i);
            if (dusman.yasiyor) {
                dusman.x += dusmanHizX;

                // Dusman kenarlara çarparsa
                if (dusman.x + dusman.genislik >= tahtaGenisligi || dusman.x <= 0) {
                    dusmanHizX *= -1;
                    dusman.x += dusmanHizX * 2;

                    // Tum dusmanları bir satır aşağı kaydır
                    for (int j = 0; j < dusmanListesi.size(); j++) {
                        dusmanListesi.get(j).y += dusmanYukseklik;
                    }
                }

                if (dusman.y >= gemi.y) {
                    oyunBitti = true;
                }
            }
        }

        // Mermiler
        for (int i = 0; i < mermiListesi.size(); i++) {
            Blok mermi = mermiListesi.get(i);
            mermi.y += mermiHizY;

            // Merminin dusmanla çarpışması
            for (int j = 0; j < dusmanListesi.size(); j++) {
                Blok dusman = dusmanListesi.get(j);
                if (!mermi.kullanildi && dusman.yasiyor && carpismaTespiti(mermi, dusman)) {
                    mermi.kullanildi = true;
                    dusman.yasiyor = false;
                    dusmanSayisi--;
                    puan += 100;
                }
            }
        }

        // Kullanılmış veya sınır dışı mermileri temizle
        while (mermiListesi.size() > 0 && (mermiListesi.get(0).kullanildi || mermiListesi.get(0).y < 0)) {
            mermiListesi.remove(0);
        }

        // Bir sonraki seviyeye geç
        if (dusmanSayisi == 0) {
            puan += dusmanSutunlari * dusmanSatirlari * 100; // Bonus puan :)
            dusmanSutunlari = Math.min(dusmanSutunlari + 1, sutunlar / 2 - 2);
            dusmanSatirlari = Math.min(dusmanSatirlari + 1, satirlar - 6);
            dusmanListesi.clear();
            mermiListesi.clear();
            dusmanlariOlustur();
        }
    }

    public void dusmanlariOlustur() {
        Random rastgele = new Random();
        for (int c = 0; c < dusmanSutunlari; c++) {
            for (int r = 0; r < dusmanSatirlari; r++) {
                int rastgeleResimIndex = rastgele.nextInt(dusmanResimListesi.size());
                Blok dusman = new Blok(
                        dusmanX + c * dusmanGenislik,
                        dusmanY + r * dusmanYukseklik,
                        dusmanGenislik,
                        dusmanYukseklik,
                        dusmanResimListesi.get(rastgeleResimIndex)
                );
                dusmanListesi.add(dusman);
            }
        }
        dusmanSayisi = dusmanListesi.size();
    }

    public boolean carpismaTespiti(Blok a, Blok b) {
        return  a.x < b.x + b.genislik &&  // A'nın sol üst köşesi B'nin sağ üst köşesine ulaşmaz
                a.x + a.genislik > b.x &&  // A'nın sağ üst köşesi B'nin sol üst köşesini geçer
                a.y < b.y + b.yukseklik && // A'nın sol üst köşesi B'nin sol alt köşesine ulaşmaz
                a.y + a.yukseklik > b.y;   // A'nın sol alt köşesi B'nin sol üst köşesini geçer
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        hareketEttir();
        repaint();
        if (oyunBitti) {
            oyunDongusu.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (oyunBitti) { // Herhangi bir tuşla yeniden başlat
            gemi.x = gemiX;
            mermiListesi.clear();
            dusmanListesi.clear();
            oyunBitti = false;
            puan = 0;
            dusmanSutunlari = 3;
            dusmanSatirlari = 2;
            dusmanHizX = 1;
            dusmanlariOlustur();
            oyunDongusu.start();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && gemi.x - gemiHizX >= 0) {
            gemi.x -= gemiHizX; // Sol bir kare hareket et
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && gemi.x + gemiHizX + gemi.genislik <= tahtaGenisligi) {
            gemi.x += gemiHizX; // Sağ bir kare hareket et
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // Mermi ateşle
            Blok mermi = new Blok(gemi.x + gemiGenislik * 15 / 32, gemi.y, mermiGenislik, mermiYukseklik, null);
            mermiListesi.add(mermi);
        }
    }
}
