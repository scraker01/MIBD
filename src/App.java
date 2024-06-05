import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class App {
    
    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
        Scanner sc = new Scanner(System.in);

        //Connection
        Connection conn = null;
        // Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try{

            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());

            String dbURL = "jdbc:sqlserver://Acer\\SQLEXPRESS;encrypt=true;trustServerCertificate=true;databaseName=JavaDatabase;IntegratedSecurity=true";
            String username = "sa";
            String password = "pass";
            
            //Connect ke sqlserver dengan menggunakan driver manager
            conn = DriverManager.getConnection(dbURL, username, password);

            //Check apakah connection berhasil
            if (conn != null) {
           
                //Siapkan Statement
                Statement stmt = conn.createStatement();
                
                start(conn,sc);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            
        } finally { 
            try {       //Tutup koneksi apabila telah seluruh action telah dilakukan
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        }
    }

    static void start(Connection conn, Scanner sc){
        //Init variabel untuk input user
        int user_Input;

        //Print to screen untuk pilihan aksi yang ingin dipilih
        System.out.printf("Masuk Sebagai:"+
                                "\n1. Pelanggan\n2. Pegawai\n"+
                                "\t-> "); user_Input= sc.nextInt();
            welcome();
            //Launch Menu Pilihan
            if (user_Input == 1){   //Menu pelanggan
                // System.out.println("Belum diimplementasikan");
                launchPelMenu(conn, sc);
            }else{                  //Menu Pegawai
                
                //Nanti implementasikan login 
                boolean isLogin = false;
                do {
                    
                    if (checkPegawai(conn, sc)) {
                        isLogin = true;
                        launchPGMenu(conn, sc);
                    
                    }
                    else {
                        System.out.println("Login gagal");
                        
                    }
                } while (!isLogin);

            //Kemungkinan nanti akan ditambahkan intermediary ketika launch menu, akan diselect berdasarkan role (pegawai & pemilik)
        }
    }

//===============================================================================================================================================

    static boolean testKap(int kap){
        if (kap<6||kap>12)return false;
        if (kap%2==1) return false;
        return true;
    }

    static int selectKap(Scanner sc, Connection conn){
        int kapasitas;
        System.out.println("Kapasitas Mesin Cuci Yang Tersedia : 6/8/10/12");
        do{
            System.out.printf("\nKapasitas Mesin Cuci\t:");kapasitas = sc.nextInt(); // 0 atau 1 (tipe bit) menandakan mesin sedang berjalan atau tidak

        }while (!testKap(kapasitas));

        return kapasitas;
    }

//===============================================================================================================================================
    /*
     * Method untuk meng-insert data ke tabel mesin cuci 
     */
    static void insertDataMesinCuci(Scanner sc, Connection conn,String nama, int kapasitas) throws SQLException{
        try{
            int idx;
            String sql;
            //Connect Statement
            Statement stmt = conn.createStatement();  

            //Print untuk Mesin Cuci dan Merek yang tersedia
            printFull(conn,"MesinCuci");
            printFull(conn,"Merek");

            //Input user untuk id merek
            System.out.printf("\nId Merek :\t");idx = sc.nextInt();
            
            //Insert Data Mesin Cuci
            String insertDP = String.format(
                "INSERT INTO MesinCuci (nama,kap,id_M) VALUES"
                +"('%s', %d, %d)",nama,kapasitas, idx);

            //Execute insert data produk ke tabel
            stmt.execute(insertDP);
  
            
            //Update harga tarif yang masih 0
            sql = String.format("UPDATE MesinCuci Set tarif = kap*1500*id_M/2 WHERE tarif = 0 ");
            stmt.execute(sql);
            
            //Print tabel mesincuci yang sudah di insert dengan data baru
            printFull(conn,"MesinCuci");

        } catch (Exception e){
            e.printStackTrace();

        } 
        
    }


    static void insertDataPelanggan(Scanner sc, Connection conn ) throws SQLException{
        try{
            int idx;
            String sql, nama, email,alamat, noHP;
            //Connect Statement
            Statement stmt = conn.createStatement();  

            //Print untuk Mesin Cuci dan Merek yang tersedia
            printFull(conn,"Kelurahan");
            System.out.printf("Nama Pelanggan : "); nama =sc.next();
            System.out.printf("Alamat Pelanggan : "); alamat =sc.next();
            System.out.printf("Email Pelanggan : "); email =sc.next();
            System.out.printf("No HP : "); noHP=sc.next();
            // System.out.printf("Lokasi Kelurahan : "); idx =sc.nextInt();


            //Input user untuk id merek
            System.out.printf("\nId Kelurahan :\t");idx = sc.nextInt();
            
            //Insert Data Mesin Cuci
            String insertDP = String.format(
                "Insert into Pelanggan(nama,no_HP',alamat,email,id_Kel) VALUES "+
                "('%s','%s','%s',%d)",nama, noHP,alamat, email,idx);

            //Execute insert data produk ke tabel
            stmt.execute(insertDP);
  
            printPelangganKel(conn);

        } catch (Exception e){
            e.printStackTrace();

        } 
        
    }
//===============================================================================================================================================
    /*
     * Method untuk mendelete data dari tabel
     * 
     * Sementara untuk mesin cuci saja
     */
    static boolean deleteDataMesinCuci(Scanner sc, Connection conn){   
        try {
            String sql;
            int idx;
            Statement stmt = conn.createStatement();


            printFull(conn,"MesinCuci");
            System.out.println("Id Mesin Cuci : ");idx =sc.nextInt();

            sql = String.format("DELETE FROM MesinCuci WHERE id_MC = %d",idx);
            stmt.execute(sql);

            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        
    }
    

//===============================================================================================================================================
//TODO : 
//Update mesin cuci
    static void updateMC(Scanner sc, Connection conn){
        try {
            

            String sql, inpYN,nama;
            int idx ,idM, kap;
            Statement stmt = conn.createStatement();
            sql = "";
            
            printFull(conn,"MesinCuci");
            // System.out.println("Atribut yang ingin dipilih : ");
            System.out.printf("\nid Mesin Cuci : ");idx =sc.nextInt();

            System.out.printf("\nKapasitas : "); kap = selectKap(sc, conn);
            printFull(conn,"Merek");
            System.out.printf("\nid Merek: ");idM =sc.nextInt();

            System.out.printf("\nUpdate Nama?[Y/N] :"); inpYN = sc.next().toLowerCase();
            
            if(inpYN.equals("")|| inpYN.toLowerCase().equals("y")){
                System.out.printf("\n Input nama (<20) :"); nama= sc.next();
                                
                    sql = String.format("UPDATE MesinCuci\n" + //
                    "SET id_M = %d, kap = %d, nama = '%s'\r\n"+
                    "WHERE id_MC = %d",idM,kap,nama,idx);
                    
            } else if (inpYN.toLowerCase().equals("n")){
                    sql = String.format("UPDATE MesinCuci\n" + //
                        "SET id_M = %d, kap = %d\r\n"+
                        "WHERE id_MC = %d",idM,kap,idx);
                    
                    
                }
                
            stmt.execute(sql);

            sql = String.format("UPDATE MesinCuci\r\n" + //
                                "SET tarif = kap*1500*id_M/2",idx);
            stmt.execute(sql);

            printFull(conn,"MesinCuci");


           
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
           
        }

    }
    static void updateMC(Scanner sc, Connection conn){
        try {
            

            String sql, inpYN,nama;
            int idx ,idM, kap;
            Statement stmt = conn.createStatement();
            sql = "";
            
            printFull(conn,"MesinCuci");
            // System.out.println("Atribut yang ingin dipilih : ");
            System.out.printf("\nid Mesin Cuci : ");idx =sc.nextInt();

            System.out.printf("\nKapasitas : "); kap = selectKap(sc, conn);
            printFull(conn,"Merek");
            System.out.printf("\nid Merek: ");idM =sc.nextInt();

            System.out.printf("\nUpdate Nama?[Y/N] :"); inpYN = sc.next().toLowerCase();
            
            if(inpYN.equals("")|| inpYN.toLowerCase().equals("y")){
                System.out.printf("\n Input nama (<20) :"); nama= sc.next();
                                
                    sql = String.format("UPDATE MesinCuci\n" + //
                    "SET id_M = %d, kap = %d, nama = '%s'\r\n"+
                    "WHERE id_MC = %d",idM,kap,nama,idx);
                    
            } else if (inpYN.toLowerCase().equals("n")){
                    sql = String.format("UPDATE MesinCuci\n" + //
                        "SET id_M = %d, kap = %d\r\n"+
                        "WHERE id_MC = %d",idM,kap,idx);
                    
                    
                }
                
            stmt.execute(sql);

            sql = String.format("UPDATE MesinCuci\r\n" + //
                                "SET tarif = kap*1500*id_M/2",idx);
            stmt.execute(sql);

            printFull(conn,"MesinCuci");


           
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
           
        }

    }

//===============================================================================================================================================
    static void startPenggunaanMC(Scanner sc, Connection conn){
        Statement stmt;
        String sql;
        int idMC, id_Pel, id_Pg;

        try {
            stmt = conn.createStatement();
            printFull(conn,"MesinCuci");
            System.out.printf("\nid Mesin Cuci : "); idMC = sc.nextInt();
            printPelangganKel(conn);
            System.out.printf("\nid Pelanggan : "); id_Pel = sc.nextInt();
            printFull(conn,"Pegawai");
            System.out.printf("\nid Pegawai : "); id_Pg = sc.nextInt();

            sql = String.format("SELECT statMC FROM MesinCuci WHERE id_MC = %d", idMC);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int stat = rs.getInt(1);

            //Sebenernya ga harus soalnya kalo dri sisi pelanggan, dia ga bisa liat yang ga aktif
            if(stat==1){
                sql = String.format("INSERT INTO Transaksi(id_MC,id_Pel, id_Pg) VALUES(%d, %d, %d)", idMC,id_Pel,id_Pg);
                stmt.execute(sql);
                
                sql = String.format("UPDATE MesinCuci SET statMC = 0 WHERE id_MC = %d ", idMC);
                stmt.execute(sql);
                
                sql = String.format("CREATE OR ALTER VIEW [Laporan Transaksi] AS\r\n" + //
                                        "SELECT id_T,tgl_T, startT,endT,durasi,biaya,Pelanggan.nama as namaPelanggan, Pelanggan.no_HP ,Pegawai.nama AS namaPegawai, MesinCuci.nama AS namaMesinCuci, tarif ,kap, Merek.nama AS merek\r\n" + //
                                        "FROM Transaksi\r\n" + //
                                        "INNER JOIN Pelanggan ON Transaksi.id_Pel = Pelanggan.id_Pel\r\n" + //
                                        "INNER JOIN Pegawai ON Transaksi.id_Pg = Pegawai.id_Pg\r\n" + //
                                        "INNER JOIN MesinCuci ON Transaksi.id_MC = MesinCuci.id_MC\r\n" + //
                                        "INNER JOIN Merek ON MesinCuci.id_M =Merek.id_M");
                stmt.execute(sql);

                System.out.println("Mesin Cuci telah diaktifkan");
            } else {
                System.out.println("Mesin Cuci tidak tersedia");
                return;
            }


        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    static void endPenggunaanMC(Scanner sc ,Connection conn){
        Statement stmt;
        String sql;
        int idMC, id_Pel, biaya;

        try {
            stmt = conn.createStatement();
            printFull(conn,"MesinCuci");
            System.out.printf("\nid Mesin Cuci : "); idMC = sc.nextInt();
            // viewTransaksi(conn,0);  //Transaksi aktif yang diprint
            // printFull(conn,"Transaksi");
            viewTransaksiAktif(conn, idMC);
            System.out.printf("\nid Pelanggan : "); id_Pel = sc.nextInt();


            sql = String.format("SELECT statMC FROM MesinCuci WHERE id_MC = %d", idMC);
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int stat = rs.getInt(1);

            //Sebenernya ga harus soalnya kalo dri sisi pelanggan, dia ga bisa liat yang ga aktif
            if(stat==0){
                sql = String.format("UPDATE MesinCuci SET statMC = 1 WHERE id_MC = %d", idMC);
                stmt.execute(sql);

                sql = String.format("SELECT id_T FROM Transaksi WHERE id_MC = %d AND id_Pel = %d AND biaya =0", idMC, id_Pel);
                rs = stmt.executeQuery(sql);

                //Majukan cursor ke row pertama
                rs.next();
                int id_T = rs.getInt(1);
                
                //Cari Id Transaksi, terus ubah endT , habis itu dapetin durasi & itung biaya

                //Peroleh end time dari mesin cuci
                sql = String.format("UPDATE Transaksi\r\n" + //
                                        "set endT = FORMAT(GETDATE(), 'hh:mm:ss')"+
                                        "WHERE id_T = %d", id_T);
                stmt.execute(sql);
                
                //Update durasi penggunaan mesin cuci
                sql = String.format("UPDATE Transaksi\r\n" + //
                                        "set durasi = DATEDIFF(N,startT,endT)"+
                                        "WHERE id_T = %d", id_T);
                stmt.execute(sql);

                sql = String.format("Select durasi FROM Transaksi WHERE id_T = %d", id_T);
                rs = stmt.executeQuery(sql);
                rs.next();
                int durasi = rs.getInt(1);
                
                sql = String.format("SELECT tarif FROM MesinCuci JOIN Transaksi ON Transaksi.id_MC = MesinCuci.id_MC WHERE id_T = %d",id_T);
                rs = stmt.executeQuery(sql);
                rs.next();
                int tarif = rs.getInt(1);
          
                if(durasi<15) {
              
                    biaya = tarif;
                }
                else {
                    biaya = ((int)Math.ceil(durasi/15)) *tarif;
                }
                //Update biaya sesuai dengan tarif dan durasi penggunaan
                sql = String.format("UPDATE Transaksi\r\n" + //
                                        "SET biaya = %d"+
                                        "WHERE id_T = %d",biaya, id_T);
                stmt.execute(sql);
                
                sql = String.format("SELECT biaya FROM Transaksi WHERE id_T = %d ", id_T);
                // int biaya = stmt.execute(sql);

                sql = String.format("CREATE OR ALTER VIEW [Laporan Transaksi] AS\r\n" + //
                                        "SELECT id_T,tgl_T, startT,endT,durasi,biaya,Pelanggan.nama as namaPelanggan, Pelanggan.no_HP ,Pegawai.nama AS namaPegawai, MesinCuci.nama AS namaMesinCuci, tarif ,kap, Merek.nama AS merek\r\n" + //
                                        "FROM Transaksi\r\n" + //
                                        "INNER JOIN Pelanggan ON Transaksi.id_Pel = Pelanggan.id_Pel\r\n" + //
                                        "INNER JOIN Pegawai ON Transaksi.id_Pg = Pegawai.id_Pg\r\n" + //
                                        "INNER JOIN MesinCuci ON Transaksi.id_MC = MesinCuci.id_MC\r\n" + //
                                        "INNER JOIN Merek ON MesinCuci.id_M =Merek.id_M");
                
                stmt.execute(sql);
                System.out.printf("\nTotal biaya jasa : %d",biaya);
                Thread.sleep(500);
                System.out.println("Mesin Cuci telah dimatikan");

            } else {
                System.out.println("Mesin Cuci sudah tersedia sebelumnya");
                return;
            }


        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

//===============================================================================================================================================
    static void viewAllTransaksi(Scanner sc,Connection conn){
        try {
            String sql, tahunStart, bulanStart, hariStart,tahunEnd, bulanEnd, hariEnd, tanggalMulai, tanggalAkhir;
            Statement stmt = conn.createStatement();
            
            System.out.printf("\nFormat : [DD/MM/YYYY]\n");
            System.out.printf("\nHari Awal  :"); hariStart = sc.next();
            System.out.printf("\nBulan Awal :"); bulanStart = sc.next();
            System.out.printf("\nTahun Awal :"); tahunStart =sc.next();
            
            System.out.printf("\n\t=======Hingga=======\n");
            System.out.printf("\nHari Akhir  :"); hariEnd = sc.next();
            System.out.printf("\nBulan Akhir :"); bulanEnd = sc.next();
            System.out.printf("\nTahun Akhir :"); tahunEnd =sc.next();

            tanggalMulai = (tahunStart.concat(bulanStart).concat(hariStart));
            tanggalAkhir = (tahunEnd.concat(bulanEnd).concat(hariEnd));

            System.out.println(tanggalMulai+"====="+tanggalAkhir);

            sql = String.format("SELECT * FROM Transaksi WHERE tgl_T > '%s' AND tgl_T < '%s'", tanggalMulai,tanggalAkhir);
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("\n=======================\n");
            ResultSetMetaData rsmd = rs.getMetaData();
    
            int numberOfColumn = rsmd.getColumnCount();
            for (int i =1; i<=numberOfColumn;i++){
                if(i>1) System.out.print("\t");
                String columnName = rsmd.getColumnName(i);
                System.out.print(columnName);
            }
            System.out.println();

            while(rs.next()){
                for (int i =1; i<=numberOfColumn;i++){
                    
                    if(i>1) System.out.print("\t");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue);
                }System.out.println();
            }
            sql = String.format("SELECT SUM(biaya) FROM Transaksi WHERE tgl_T > '%s' AND tgl_T < '%s'", tanggalMulai,tanggalAkhir);
            rs = stmt.executeQuery(sql);
            rs.next();

            int pendapatan = rs.getInt(1);
            
            System.out.printf("\n\nPendapatan %s/%s/%s - %s/%s/%s = %d",hariStart,bulanStart,tahunStart,hariEnd,bulanEnd,tahunEnd,pendapatan);

            System.out.println("\n=======================\n");

            
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
//

    
    static void viewTransaksiAktif(Connection conn, int idMC){
        try {
            Statement stmt = conn.createStatement();
            String sql;
            ResultSet rs;
            
            sql = String.format("SELECT * FROM Transaksi WHERE id_MC = %d",idMC);
            rs = stmt.executeQuery(sql);
            
            System.out.println("\n=======================\n");
            ResultSetMetaData rsmd = rs.getMetaData();
    
            int numberOfColumn = rsmd.getColumnCount();
            for (int i =1; i<=numberOfColumn;i++){
                if(i>1) System.out.print("\t");
                String columnName = rsmd.getColumnName(i);
                System.out.print(columnName);
            }
            System.out.println();

            while(rs.next()){
                for (int i =1; i<=numberOfColumn;i++){
                    
                    if(i>1) System.out.print("\t");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue);
                }System.out.println();
            }
            System.out.println("\n=======================\n");

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
//===========================================================================================================

    /*
     * Method untuk memgvisualisasikan seluruh table yang tersedia pada database
     * 
     */
    static void checkAvailableTable(Connection conn){
        try{
            DatabaseMetaData md = conn.getMetaData();
            String[] types = {"TABLE"};
            ResultSet rs = md.getTables(null, "dbo", "%", types);
            while(rs.next()){
                System.out.println(rs.getString("TABLE_NAME"));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }


    /*
     * Method untuk menerima input user pada awal program
     */
    static int isContinuing(Scanner sc){
        System.out.printf(
                            "\n1. Continue\n2. Exit\n"+
                            "\t-> ");
        int inp = sc.nextInt();
        System.out.println();
        return inp;

    }

    /*
     * Method untuk memvisualisasikan keseluruhan isi dari tabel Mesin Cuci
     */
    static void printFull(Connection conn, String namaTabel) {
        try{
            System.out.println("\n=======================\n");
            Statement stmt = conn.createStatement();
            String sql = String.format("SELECT * FROM %s",namaTabel);
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
    
            int numberOfColumn = rsmd.getColumnCount();
            for (int i =1; i<=numberOfColumn;i++){
                if(i>1) System.out.print("\t");
                String columnName = rsmd.getColumnName(i);
                System.out.print(columnName);
            }
            System.out.println();

            while(rs.next()){
                for (int i =1; i<=numberOfColumn;i++){
                    
                    if(i>1) System.out.print("\t");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue);
                }System.out.println();
            }
            System.out.println("\n=======================\n");

        } catch(Exception e){
            e.printStackTrace();
        }
    }


    /*
     * Print spesifik
     */

    static void printTarif(Connection conn) {
        try{
            System.out.println("\n=======================\n");
            Statement stmt = conn.createStatement();
            String sql = String.format("SELECT id_MC, MesinCuci.nama, tarif, kap, Merek.nama AS merek FROM MesinCuci JOIN Merek ON MesinCuci.id_M = Merek.id_M");
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
    
            int numberOfColumn = rsmd.getColumnCount();
            for (int i =1; i<=numberOfColumn;i++){
                if(i>1) System.out.print("\t");
                String columnName = rsmd.getColumnName(i);
                System.out.print(columnName);
            }
            System.out.println();

            while(rs.next()){
                for (int i =1; i<=numberOfColumn;i++){
                    
                    if(i>1) System.out.print("\t");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue);
                }System.out.println();
            }
            System.out.println("\n=======================\n");

        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    static void printMesinCuciTersedia(Connection conn) {
        try{
            System.out.println("\n=======================\n");
            Statement stmt = conn.createStatement();
            String sql = String.format("SELECT id_MC, MesinCuci.nama, kap, Merek.nama AS merek FROM MesinCuci JOIN Merek ON MesinCuci.id_M = Merek.id_M WHERE statMC = 1");
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
    
            int numberOfColumn = rsmd.getColumnCount();
            for (int i =1; i<=numberOfColumn;i++){
                if(i>1) System.out.print("\t");
                String columnName = rsmd.getColumnName(i);
                System.out.print(columnName);
            }
            System.out.println();

            while(rs.next()){
                for (int i =1; i<=numberOfColumn;i++){
                    
                    if(i>1) System.out.print("\t");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue);
                }System.out.println();
            }
            System.out.println("\n=======================\n");

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    static void printPelangganKel(Connection conn) {
        try{
            System.out.println("\n=======================\n");
            Statement stmt = conn.createStatement();
            String sql = String.format("SELECT id_Pel, Pelanggan.nama, no_HP,alamat, email, Kelurahan.nama FROM Pelanggan JOIN Kelurahan ON Pelanggan.id_Kel = Kelurahan.id_Kel");
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
    
            int numberOfColumn = rsmd.getColumnCount();
            for (int i =1; i<=numberOfColumn;i++){
                if(i>1) System.out.print("\t");
                String columnName = rsmd.getColumnName(i);
                System.out.print(columnName);
            }
            System.out.println();

            while(rs.next()){
                for (int i =1; i<=numberOfColumn;i++){
                    
                    if(i>1) System.out.print("\t");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue);
                }System.out.println();
            }
            System.out.println("\n=======================\n");

        } catch(Exception e){
            e.printStackTrace();
        }
    }

//===========================================================================================================================================

    /*
     * Method yang digunakan untuk menyimpan script inisialisasi keseluruhan tabel 
     * 
     * Jangan dipakai dulu, karena kalau nanti implementasi pelanggan dll, ini ga mungkin dilakuin karean FOREIGN KEY
     */

    static void launchPGMenu(Connection conn, Scanner sc){
        int user_Input;

        try {
            
            do{
                user_Input = isContinuing(sc); 
                
                if(user_Input==1){
                    //Init variabel untuk aksi pilihan user
                    int user_Action; 
                    System.out.printf("Select Data-Related Action:"+
                                    "\n1. Insert Data\n2. Delete Data\n3. Update Data\n4. Show MesinCuci\n5. Kelola Transaksi Pelanggan\n6. Show Transaction\n7. Reset Status\n8. Log Out\n"+
                                    "\t-> ");
                    user_Action = sc.nextInt();
                    
                    // System.out.println("\n\n ======Available Table======\n");
                    
                    //Print untuk tabel yang tersedia
                    // checkAvailableTable(conn);
    
                    if(user_Action == 1){       //Pilihan untuk input data
                        String dbName;
                        checkAvailableTable(conn);
                        System.out.printf("\n\n Select Table: ");
                        dbName = sc.next().toLowerCase();
                        switch (dbName) {
                            case "mesincuci":
    
                                //Print katalog Mesin Cuci & Tarif
                                printFull(conn,"MesinCuci");
    
                                //Minta input untuk id, nama dan status mesin yang diinginkan
                                String nama;
                                int kapasitas;
                                
                                //Input nama untuk mesin cuci
                                System.out.printf("\nNama Mesin Cuci\t:");nama = sc.next();
    
                                //Input kapasitas mesin cuci, hanya berlaku untuk kelipatan 2 pada 6 hingga 12

                                kapasitas = selectKap(sc,conn);
                                    
                                
                                //Insert data tersebut ke dalam tabel mesin cuci
    
                                insertDataMesinCuci(sc, conn, nama, kapasitas);
                                
                                break;
                            case "pelanggan":
                                printPelangganKel(conn);

                                insertDataPelanggan(sc, conn);
                                
                            default:
                                break;
                        }

                    } else if(user_Action ==3) {        //Update Tarif
                        updateMC(sc, conn);
                        
                    } else if(user_Action ==2) {        //Delete data

                        if(deleteDataMesinCuci(sc, conn)) System.out.println("Delete Successful");
                        else System.out.println("Delete Failed");
                        
                    } else if(user_Action==4){

                        printFull(conn,"MesinCuci");
                    } else if(user_Action==5){

                        System.out.println("\n1. Mulai Transaksi Mesin Cuci\n2. Akhiri Transaksi Mesin Cuci\n");
                        System.out.println("\t->");int inpTransaksi= sc.nextInt();

                        if(inpTransaksi==1){
                            startPenggunaanMC(sc, conn);
                        } else if (inpTransaksi==2){
                            endPenggunaanMC(sc, conn);
                        }


                    } else if(user_Action==6){
                        // printFull(conn,"Transaksi");
                        System.out.println("\n1. Seluruh Transaksi\n2. Transaksi dan pendapatan pada rentang tanggal\n");
                        System.out.println("\t->");int inpTransaksi= sc.nextInt();

                        if(inpTransaksi==1){
                            printFull(conn, "[Laporan Transaksi]");
                        } else if (inpTransaksi==2){
                            viewAllTransaksi(sc, conn);
                        }

                    } else if(user_Action==7){
                        resetStatusMesinCuci(conn);
                    }else if(user_Action==8){
                        start(conn, sc);
                    }
                    
                    else {
                        
                        // fixBiaya(conn, sc);
                        System.out.println();
                        
                    }
                    
                    System.out.println();
                //Hentikan Program dengan pilihan
                }else if(user_Input==2){
                    System.out.println("\n===========Thank You===========");
    
                } else { //Hentikan program secara paksa
                    System.out.println("\n!!ACTION INVALID!!");
                }
                
                //Jeda sebelum melakukan aksi selanjutnya
                Thread.sleep(1000);
            }while(user_Input==1);
        } catch (Exception e) {
         
            e.printStackTrace();
        }
    
    }

//===============================================================================================================================================
    static boolean checkPegawai(Connection conn, Scanner sc){
        String nama,username, pass, sql,checkerNama, checkerUser, checkerPass;
        ResultSet rs;
        ResultSetMetaData rsmd;

        try {
            System.out.printf("\nMasukkan Nama Pegawai : "); nama = sc.next();
            System.out.printf("\nMasukkan username : "); username = sc.next();
            System.out.printf("\nMasukkan password : "); pass = sc.next();

            sql = String.format("SELECT nama,username,pass FROM Pegawai WHERE nama LIKE '%s' ",nama);
            
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            rsmd = rs.getMetaData();
            rs.next();

            checkerNama = rs.getString(1);
            checkerUser = rs.getString(2);
            checkerPass = rs.getString(3);
            
            
            if(nama.equals(checkerNama)&&username.equals(checkerUser)&& pass.equals(checkerPass)) return true;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return false;
        
    }

    static void resetStatusMesinCuci(Connection conn){
        try {
            Statement stmt = conn.createStatement();
            String sql;

            sql = String.format("UPDATE MesinCuci SET statMC = 1");
            stmt.execute(sql);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


//====================================================================================================================
    static void launchPelMenu(Connection conn, Scanner sc){
        int user_Input;

        try {
            
            do{
                user_Input = isContinuing(sc); 
                
                if(user_Input==1){
                    //Init variabel untuk aksi pilihan user
                    int user_Action; 
                    System.out.printf("Select Data-Related Action:"+
                                    "\n1. Lihat Tarif\n2. Lihat Mesin Cuci yang Tersedia\n"+
                                    "\t-> ");
                    user_Action = sc.nextInt();
                    
                    if(user_Action==1){
                        printTarif(conn);
                    } else if (user_Action==2){
                        printMesinCuciTersedia(conn);
                    }
                }
                //Jeda sebelum melakukan aksi selanjutnya
                Thread.sleep(1000);
            }while(user_Input==1);
        } catch (Exception e) {
         
            e.printStackTrace();
        }
    }
    
    
    static void welcome(){
        System.out.printf("\r\n" + 
                        "░  ░░░░  ░░        ░░  ░░░░░░░░░      ░░░░      ░░░  ░░░░  ░░        ░\r\n" + //
                        "▒  ▒  ▒  ▒▒  ▒▒▒▒▒▒▒▒  ▒▒▒▒▒▒▒▒  ▒▒▒▒  ▒▒  ▒▒▒▒  ▒▒   ▒▒   ▒▒  ▒▒▒▒▒▒▒\r\n" + //
                        "▓        ▓▓      ▓▓▓▓  ▓▓▓▓▓▓▓▓  ▓▓▓▓▓▓▓▓  ▓▓▓▓  ▓▓        ▓▓      ▓▓▓\r\n" + //
                        "█   ██   ██  ████████  ████████  ████  ██  ████  ██  █  █  ██  ███████\r\n" + //
                        "█  ████  ██        ██        ███      ████      ███  ████  ██        █\r\n" + //
                        "                                                                      \r\n");
                
        try {
            System.out.println("\n\n========================================================================\n\n");

            Thread.sleep(1000);
            System.out.printf("░   ░░░  ░░        ░░  ░░░░  ░░░      ░░░        ░░░░░░░░░      ░░░░      ░░░       ░░░       ░░\r\n" + //
                            "▒    ▒▒  ▒▒  ▒▒▒▒▒▒▒▒  ▒  ▒  ▒▒  ▒▒▒▒  ▒▒▒▒▒  ▒▒▒▒▒▒▒▒▒▒▒  ▒▒▒▒  ▒▒  ▒▒▒▒  ▒▒  ▒▒▒▒  ▒▒  ▒▒▒▒  ▒\r\n" + //
                            "▓  ▓  ▓  ▓▓      ▓▓▓▓        ▓▓  ▓▓▓▓▓▓▓▓▓▓▓  ▓▓▓▓▓▓▓▓▓▓▓  ▓▓▓▓▓▓▓▓  ▓▓▓▓  ▓▓       ▓▓▓       ▓▓\r\n" + //
                            "█  ██    ██  ████████   ██   ██  ████  █████  ███████████  ████  ██  ████  ██  ███  ███  ███████\r\n" + //
                            "█  ███   ██        ██  ████  ███      ███        █████████      ████      ███  ████  ██  ███████");
    
        
        System.out.println("\n\n================================================================================================\n\n");
                            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}


    