import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        // pencere değişkenleri
        int kareBoyutu = 32;
        int satirSayisi = 16;
        int sutunSayisi = 16;
        int tahtaGenisligi = kareBoyutu * sutunSayisi; // 32 * 16 = 512px
        int tahtaYuksekligi = kareBoyutu * satirSayisi; // 32 * 16 = 512px

        JFrame pencere = new JFrame("Uzay Savaşçıları");
        // pencere.setVisible(true);
        pencere.setSize(tahtaGenisligi, tahtaYuksekligi);
        pencere.setLocationRelativeTo(null);
        pencere.setResizable(false);
        pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SpaceInvaders SpaceInvaders = new SpaceInvaders();
        pencere.add(SpaceInvaders);
        pencere.pack();
        SpaceInvaders.requestFocus();
        pencere.setVisible(true);

    }
}
