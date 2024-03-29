package utils;

import dojoarvorebmais.Entidade;
import dojoarvorebmais.Metadados;
import dojoarvorebmais.NoFolha;
import dojoarvorebmais.NoInterno;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/*
 * Classe que possui métodos úteis para manipulação de arquivos
 */
public class Arquivos {

    /**
     * Abre um arquivo para leitura
     * @param nomeArquivo: nome do arquivo a ser aberto
     * @return RandomAccessFile do arquivo
     */
    public static DataInputStream abreArquivoEntrada(String nomeArquivo)
            throws FileNotFoundException {
        return new DataInputStream(new BufferedInputStream(
                new FileInputStream(nomeArquivo)));
    }

    /**
     * Deleta um arquivo
     * @param nomeArquivo: nome do arquivo a ser deletado
     */
    public static void deletaArquivo(String nomeArquivo)  {
        File arquivo = new File(nomeArquivo);
        if (arquivo.exists()) {
            arquivo.delete();            
        }        
    }

    /**
     * Abre um arquivo para gravação
     * @param nomeArquivo: nome do arquivo a ser aberto
     * @return RandomAccessFile que será aberto
     */
    public static RandomAccessFile abreArquivoSaida(String nomeArquivo)
            throws FileNotFoundException {
        return new RandomAccessFile(new File(nomeArquivo), "rw");
    }

    /**
     * Fecha um arquivo
     * @param arquivo Arquivo que será fechado
     */
    public static void fechaArquivo(Closeable arquivo) throws IOException {
        arquivo.close();
    }    

    /*
     * Salva um registro de um Entidade em um arquivo
     * @param nomeArquivo Nome do arquivo onde o registro será salvo
     * @param lista Lista de registros do tipo Entidade
     */
    public static <T extends Entidade> void salva(String nomeArquivo, int pos, List<T> lista) throws FileNotFoundException, IOException {
        RandomAccessFile out = new RandomAccessFile(new File(nomeArquivo), "rw");
        out.seek(pos);
        for (T entidade : lista) {            
            entidade.salva(out);            
        }
        out.close();
    }

    public static void salva(String nomeArquivo, Metadados m) throws FileNotFoundException, IOException {
        //metadados sempre sao salvos no início do arquivo
        RandomAccessFile out = new RandomAccessFile(new File(nomeArquivo), "rw");
        m.salva(out);
        out.close();
    }

    /*
     * Lê nós folha do arquivo de dados
     * @param nomeArquivo Nome do arquivo que será lido
     * @return Lista de objetos do tipo NoFolha, lidos do arquivo
     */
    public static List<NoFolha> leNosFolha(String nomeArquivo) throws FileNotFoundException, IOException {
        List<NoFolha> result = new ArrayList<NoFolha>();

        RandomAccessFile in = new RandomAccessFile(new File(nomeArquivo), "r");        
        try {
            while (true) {
                result.add(NoFolha.le(in));
            }
        } catch (EOFException e) {
        } finally {
            in.close();
        }

        return result;
    }

    public static List<NoInterno> leNosInternos(String nomeArquivo) throws FileNotFoundException, IOException {
        List<NoInterno> result = new ArrayList<NoInterno>();

        RandomAccessFile in = new RandomAccessFile(new File(nomeArquivo), "r");

        try {
            in.seek(Metadados.TAMANHO);            
            while (true) {
                result.add(NoInterno.le(in));                
            }
        } catch (EOFException e) {
        } finally {
            in.close();
        }

        return result;
    }

    /*
     * Lê metadados da árvore B+ (pontRaiz, raizFolha)
     * @param nomeArquivo Nome do arquivo de índice
     * @return pontRaiz 
    */
    public static Metadados leMetadadosArvoreBMais(String nomeArquivo) throws FileNotFoundException, IOException {
        Metadados m = new Metadados();

        RandomAccessFile in = new RandomAccessFile(new File(nomeArquivo), "r");
        try {
            m.pontRaiz = in.readInt();
            m.raizFolha = in.readBoolean();
        } catch (EOFException e) {}
        in.close();

        return m;
    }

    /*
     * Lê uma arquivo e coloca seu conteúdo em uma string
     * @param nomeArquivo Nome do arquivo que será lido
     * @return String com o conteúdo do arquivo
     */
    @Deprecated
    public static String arquivoToString(String nomeArquivo) throws FileNotFoundException, IOException {
        String result = new String();

        FileInputStream in = null;
        try {
            in = new FileInputStream(nomeArquivo);
            int c;
            while ((c = in.read()) != -1) {
                result = result + c;
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return result;
    }
}
