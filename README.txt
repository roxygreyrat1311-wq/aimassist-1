CARA BUILD (butuh Java 21 + internet)

1. Buka https://fabricmc.net/develop, pilih Minecraft 1.21.11, generate template.
2. Dari template itu, copy folder "gradle" + file "gradlew" dan "gradlew.bat"
   ke dalam folder project ini (biar wrapper Gradle-nya pas).
3. Buka terminal di folder project ini, jalankan:
      Windows : gradlew build
      Mac/Linux: ./gradlew build
4. Hasil jar ada di build/libs/aimassist-1.0.0.jar  (JANGAN ambil yang -sources)
5. Taruh jar itu + Fabric API (versi 1.21.11) di folder mods.

Kontrol di game:  R = ON/OFF,  Right Shift = menu setting.
