# ✅ InvotraxApp – Projektterv és Fejlesztési lista

Ez a dokumentum összegyűjti a projekt aktuális állapotát és a még hátralévő feladatokat. A lista checkbox formátumban szerepel, hogy pipálható legyen, ha egy-egy rész elkészült.

---

## 1. 🔁 CRUD műveletek (hiányzó entitásokhoz)

- [x] InventoryItem CRUD (DTO + Controller + Service)
- [x] InvoiceNumberSequence kezelés (Admin CRUD vagy csak Service szint)
- [x] InvoiceType CRUD (kód + megjelenített név)
- [x] PaymentMethod CRUD
- [x] ProductType CRUD (kód + név + készletkezelést végző flag)
- [x] SerialNumber kezelés (csak olvasás vagy admin CRUD?)
- 
---

## 2. 📦 Üzleti logika / folyamatok

- [ ] Eladás (számlázás) → készlet csökkentés (ProductType alapján)
- [x] Bevételezés → készlet növelés
- [ ] SerialNumber kezelés: kötelező kitölteni, ha be van állítva
- [ ] SerialNumber `used` státusz kezelése eladásnál/stornónál
- [ ] Stornózás (Invoice és készlet visszaállítása)
- [ ] Partner egyenleg számítása (számlák - fizetések alapján)
- [ ] ÁFA-kategóriánkénti összegzés PDF-ben (27%, 5%, stb.)
- [ ] Vonalkód generálás (EAN-13, prefix kezelés)

---

## 3. 🛡️ Validációk / üzleti szabályok

- [ ] Egyedi mezők validációja (pl. SKU, EAN, adószám)
- [ ] ProductType alapján: csak "áru" típushoz tartozik készlet
- [ ] SerialNumber: ne választhasson olyat, ami már `used = true`
- [ ] SerialNumber ellenőrzés: mennyiség == darabszám
- [ ] PDF jelszavas mentés, visszafejtés jelszómentes példányba

---

## 4. 📤 API bővítések / Frontend támogatás

- [ ] InvoiceDto → jelszómentes PDF Base64-ben (`pdfContentBase64`)
- [ ] `GET /api/invoices/{id}/pdf` endpoint jelszó nélküli PDF-re
- [ ] Partner keresés: név töredék, adószám alapján
- [ ] Termék keresés: név, cikkszám, EAN alapján
- [ ] `SellerCompanyProfile` egyetlen példány kezelése frontend felől

---

## 5. 🐳 Docker és fájlkezelés

- [ ] Volume mappa: `/app/invoices` (PDF-ek)
- [ ] PDF mentés fájlként: `invoiceNumber.pdf`
- [ ] PDF path mentése az Invoice entitásban
- [ ] Fájl beolvasása és jelszó nélküli példány generálása
- [ ] PDF letöltő API végpont (opcionális: fájlként vagy Base64-ként)

---

## 🔄 Extra / későbbi fejlesztések (nem első körös)

- [ ] Szállítói számla modul
- [ ] Több valutás támogatás
- [ ] Több nyelvű felület és számlák
- [ ] Jogosultságkezelés (felhasználók, szerepkörök)
- [ ] Teljes logolás: ki mikor mit csinált (ha lesz user)

---


