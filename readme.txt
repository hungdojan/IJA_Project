Implementace projektu z předmětu IJA
------------------------------------
Autori:
- Hung Do (xdohun00)
- Petr Kolarik (xkolar79)

Prerekvizity
------------
- Maven
- Java JDK 11

Překlad a spuštění programu
---------------------------
Pro vytvoření spustitelného JAR souboru je potřeba, aby daný uživatel mel nainstalovaný Maven (a Javu JDK).
Kompilace a vytvoření se provádí za pomocí příkazu:
```bash
mvn clean
mvn package
```
Tento příkaz vytvoří soubor `UmlEditor-1.0.SNAPSHOT.jar`. Pro jeho následné spuštění stačí zadat:
```bash
java -jar target/UmlEditor-1.0.SNAPSHOT.jar
```

Ovládání programu
-----------------
Po spuštění aplikace se uživateli nově vytvoří instance třídního diagramu. Z levého panelu může kliknutím na obrázky přidat
předem vytvořené šablony, které může později upravovat. Pro úpravu jedno objektu je potřeba značit vybraný element. Element
se obarví (značení toho, se který elementem se pracuje) a objeví se nastavovací panel na pravé straně. Uživatel může přidávat,
měnit a mazat různé atributy elementu.

Práce s relacemi
----------------
Diagram editor umožňuje přidávat vztahy/relace mezi elementy. Pro přidání vztahu uživatel klikne na **Relations** -> "Add relation".
Objeví se textové pole a tlačítko "Delete". Tlačítko slouží k mazání vztahu, textové pole očekává jmého třidy, se kterým je vybraný element ve vztahu.
Nakonec uživatel zmáčkne "Enter" a vztah se vytvoří.

Chybějící implementace
----------------------
- sekvenční diagram
- více druhů vztahů mezi třídními elementy
- načítání ze a ukládání do souboru
- a drobné chyby v programu