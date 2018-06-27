package br.com.crhomaPixel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainForm {

    private JTextField pastaComImagens;
    private JPanel panelMain;

    public static void main(String[] args){
        JFrame f = new JFrame("MainForm");
        f.setContentPane(new MainForm().panelMain);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);

        MainForm m = new MainForm();  //Cria o objeto para usar os métodos dessa classe (br.com.crhomaPixel.Main).

        StringBuilder todosOsFrames = new StringBuilder();
        int velocidadeAnimacao = 10;

        String caminhoDaPastaComAsImagens = "C:\\Users\\wwwkr\\Desktop\\Chroma LOL\\Animacoes\\Explosão\\frames";
        String[] nomeDosArquivos = m.PegarNomeDosArquivos(new File(caminhoDaPastaComAsImagens));
        for (String caminhoImagem : nomeDosArquivos) {

            //PEGA A IMAGEM EM FORMATO BufferedImage.
            BufferedImage imagem = m.ObtemImagemBuff(caminhoImagem);  //Chama o método ObtemImagemBuff.

            StringBuilder frame = m.criaStringDosPixels(imagem);

            //Adiciona a linha que adiciona o Frame no código do C++.
            todosOsFrames.append(frame + "\ng_ChromaSDKImpl.CreateKeyboardEffectImpl(ChromaSDK::Keyboard::CHROMA_NONE, &Effect, &Frame);");
            todosOsFrames.append("g_ChromaSDKImpl.AddToGroup(EffectId, Frame, " + velocidadeAnimacao + ");");
        }
        //CRIA O ARQUIVO TXT.
        String nomeDoArquivo = "Animacao";
        String localDoArquivo = "C:\\Users\\wwwkr\\Desktop\\Chroma LOL\\Animacoes\\Explosão";
        File arquivoTxt = m.criaArquivoTxt(nomeDoArquivo, localDoArquivo);

        m.escreveNoArquivoTxt(arquivoTxt, String.valueOf(todosOsFrames));

        System.out.println("Pronto.");

    }

    public String[] PegarNomeDosArquivos(final File pasta){
        File[] arquivos = pasta.listFiles();

        if (arquivos != null) {
            String[] nomeDosArquivos = new String[arquivos.length];
            for (int i=0; i<arquivos.length; i++) {
                if (arquivos[i].isFile()) {
                    if (!arquivos[i].isHidden()) {
                        nomeDosArquivos[i] = pasta.getPath() + "\\" + arquivos[i].getName();  //Pega nome dos arquivos.
                    }
                }
            }
            return nomeDosArquivos;
        }else{
            System.out.println("Nenhum arquivo encontrado!");
            return null;
        }
    }

    public BufferedImage ObtemImagemBuff(String caminhoImagem){
        File arquivoImagem = new File(caminhoImagem);  //Pega a imagem como objeto para podermos trabalhar com ela.
        BufferedImage imagem = null;

        try {
            imagem = ImageIO.read(arquivoImagem);  //Retorna a imagem decodificada.
        } catch (IOException e) {
            System.out.println("Imagem não encontrada no caminho: " + caminhoImagem + ". Erro: " + e);
            e.printStackTrace();
        }
        return imagem;
    }

    public StringBuilder criaStringDosPixels(BufferedImage imagem){

        int altura = 0, largura = 0;
        StringBuilder pixels = new StringBuilder();
        for (int a = 0; a < 6; a++) {
            for (int l = 0; l < 25; l++) {

                Color corDoPixel = new Color(imagem.getRGB(largura,altura));

                int red = corDoPixel.getRed();
                int green = corDoPixel.getGreen();
                int blue = corDoPixel.getBlue();

                //Se a imagem estiver em HD (1280x720p) ele pegará apenas os píxels de uma matriz equivalente a 25x6p.
                if((red != 0) || (green != 0) || (blue != 0)) {  //Se tudo for igual a zero, não precisa escrever para polpar linha.
                    pixels.append("Effect.Color[" + a + "][" + (l + 1) + "]=RGB(" + red + "," + green + "," + blue + ");");
//                    pixels.append(System.getProperty("line.separator"));
                }

                /*if(l == 24) { //Para separar a altura com uma quebra extra de linha.
                    pixels.append(System.getProperty("line.separator"));
                }*/

                if(l != 24) {
                    largura += 51;
                    //Quando a altura chegar no máximo (6), ela deve ser zerada para pegar a altura zero por diante da próxima largura.
                }else{
                    largura = 0;
                }
            }
            altura += 120;  //Só vai pra próxima largura depois de rodar todas as alturas da posição requisitada.
        }
        return pixels;
    }

    public File criaArquivoTxt(String nomeDoArquivo, String localDoArquivo){
        String caminhoDoArquivo = localDoArquivo + nomeDoArquivo + ".txt";
        File file = null;
        try {
            file = new File(caminhoDoArquivo);

            if (file.createNewFile()){
                System.out.println("Arquivo Criado.");
            }else{
                System.out.println("Arquivo já existe Deseja sobrescrever?");  //IMPLEMENTAR DEPOIS.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void escreveNoArquivoTxt(File arquivo, String texto){
        try {
            FileWriter fileWriter = new FileWriter(arquivo);
            fileWriter.write(texto);
            fileWriter.close();
        }catch (IOException e) {
            System.out.println("Não foi possível escrever no arquivo.");
            //exception handling left as an exercise for the reader
        }
    }
}
