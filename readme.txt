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

Sekvenční diagram se může vygenerovat pomocí tlačítka `Create sequence diagram` na levém panelu dole.
To vytvoří nový tab s novou instancí sekvenčního diagramu. V něm uživatel může vytvářet nové objekty a
jednotlivé zprávy mezi objekty. Objekty se po označení dají editovat či smazat pomocí **DELETE** tlačítka
na klávesnici. Tímto způsobem se dá smazat i elementy v diagramu tříd.

Práce s relacemi
----------------
Diagram editor umožňuje přidávat vztahy/relace mezi elementy. Pro přidání vztahu uživatel klikne na **Relations** -> "Add relation".
Objeví se textové pole a tlačítko "Delete". Tlačítko slouží k mazání vztahu, textové pole očekává jmého třidy, se kterým je vybraný element ve vztahu.
Nakonec uživatel zmáčkne "Enter" a vztah se vytvoří.

Načítání a ukládání do souboru
------------------------------
Program povoluje i načítání a ukládání do souborů formátu JSON. To se řeší pomocí klávesových zkratek `Ctrl+S`
pro uložení souboru a `Ctrl+O` pro načtení souboru.