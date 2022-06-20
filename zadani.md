# Zadání projektu

**Zadání: Navrhněte a implementujte aplikaci pro zobrazení a editaci diagramů tříd a sekvenčního diagramu.**

Poznámka: Zadání definuje podstatné vlastnosti aplikace, které musí být splněny. Předpokládá se, že detaily řešení si doplní řešitelské týmy. Nejasnosti v zadání řešte, prosím, primárně na k tomu příslušném Fóru.

## Specifikace základních požadavků

### Obecné požadavky
- Aplikace umožňuje načítat, editovat a ukládat diagramy tříd a sekvenční diagramy z jazyka UML.
- Pro zápis diagramů do souboru zvolte libovolnou vhodnou syntax, inspirace např [zde](https://real-world-plantuml.com/umls/4613222493585408)
- Základní mód aplikace je práce s jedním diagramem tříd, ke kterému může být zobrazeno více sekvenčních diagramů modelujících různé scénáře vycházející z diagramu tříd. Prvky sekvenčního diagramu odpovídají diagramu tříd – objekt v sekvenčním diagramu je instancí třídy z diagramu tříd, objekt přijímá zprávu, pokud třída objektu implementuje odpovídající metodu apod.
- Při interaktivních zásazích (editaci) musí být zohledněna provázanost diagramu tříd a sekvenčních diagramů. Např. při změně názvu třídy v diagramu tříd se změní i názvy v sekvenčních diagramech.
- Implementujte operaci undo (alespoň pro základní editační operace, např. vytvoření třídy, operace, změna názvu).
Diagram tříd
- Aplikace načte ze souboru textové parametry diagramu třídy a zobrazí jej se všemi potřebnými komponentami, tj. název třídy, atributy s datovými typy vč. podpory čtyř možných modifikátorů přístupu ( + , - , # , ~ ) a metody. Obdobně pro rozhraní.
- Třídy/Rozhraní mohou být dále propojeny pomocí vztahů: asociace, agregace, kompozice a generalizace.
- V případě generalizace budou přepsané metody v odvozených třídách vizuálně odlišeny.
### Sekvenční diagram
- Aplikace načte textové parametry sekvenčního diagramu a zobrazí jej se všemi potřebnými komponentami (časová osa, objekty).
- Objekty mohou spolu dále interagovat pomocí zpráv, které mohou být těchto typů: synchronní zpráva, asynchronní zpráva, návrat zprávy, tvorba objektu, zánik (uvolnění) objektu.
- Implementujte podporu aktivace, vytváření a rušení objektů.
### Řešení nekonzistence mezi sekvenčními diagramy a diagramem tříd
- Pokud se při načítání diagramů ze souborů nalezne nekonzistence, diagram se přesto načte a zobrazí celý – části, které jsou nekonzistentní, se graficky zvýrazní (např. barevně).
- Pokud by interaktivní zásah způsobil nekonzistenci (v sekvenčním diagramu chci zaslat zprávu, která neodpovídá diagramu tříd, v diagramu tříd ruším třídu, jejíž instance se používají v sekvenčním diagramu apod.), lze to řešit následovně:
    - aplikace na to upozorní a dá na výběr, zda skutečně provést
    - zruší se vazba mezi prvky a tyto prvky se graficky zvýrazní
    - aplikace nabídne možnost doplnit metodu do třídy
    - …
- Je nutné, aby výše uvedené nekonzistence aplikace detekovala a nabídla řešení. Konkrétní způsob řešení je ponechán na řešitelském týmu.

## Rozšíření pro tříčlenný tým

1. Implementujte podporu diagramu komunikace za stejných podmínek jako sekvenční diagram (ukládání a načítání ze souboru, provázanost s diagramem tříd). Diagram komunikace musí obsahovat minimálně objekty, spojení, zprávy (stejných typů jako u sekvenčního diagramu včetně číslování zpráv) a účastníky (users).
2. Sofistikované řešení nekonzistencí - nestačí pouze označit, ale, pokud je to možné, nabídnout a provést řešení (přidání metody do třídy apod.)

## Minimální požadavky pro získání zápočtu

1. Návrh a (alespoň minimalistická) implementace všech základních požadavků uvedených ve specifikaci.
2. Součástí hodnocení bude způsob zpracování požadavků a kvalita návrhu a implementace.
