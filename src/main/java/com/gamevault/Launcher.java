package com.gamevault;

// Classe intermédiaire nécessaire pour que le fat JAR fonctionne avec JavaFX.
// Le ClassLoader du JAR ne reconnaît pas MainApp (qui étend Application) comme
// point d'entrée valide sans module-path. Ce wrapper contourne cette limitation.
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
