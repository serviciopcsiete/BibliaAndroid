#!/usr/bin/env python3
"""Convert an eBible.org USFM ZIP into app/src/main/assets/bibles/<id>.json.
Usage:
  python tools/import_usfm.py path/to/spaRV1909_usfm.zip rv1909
  python tools/import_usfm.py path/to/engwebp_usfm.zip web
The converter preserves verse text while removing USFM markup needed only for typesetting.
"""
from pathlib import Path
import zipfile,re,json,sys
if len(sys.argv)!=3: raise SystemExit("Uso: import_usfm.py <archivo.zip> <rv1909|web>")
zip_path=Path(sys.argv[1]); version=sys.argv[2]
root=Path(__file__).resolve().parents[1]
book_re=re.compile(r"^\\id\s+([A-Z0-9]{3})")
chapter_re=re.compile(r"^\\c\s+(\d+)")
verse_re=re.compile(r"^\\v\s+(\d+[a-z]?)\s+(.*)")
marker_re=re.compile(r"\\[a-z0-9+*]+(?:\s+[^\\]*?)?(?=\\|$)")
inline=re.compile(r"\\(?:w|add|nd|qt|wj|k|it|bd|bdit|sc|sup|pn|ord|no|em)\*?|\\zaln-[se][^\\]*?\\\*")
notes=re.compile(r"\\f\s.*?\\f\*|\\x\s.*?\\x\*")
rows=[]
with zipfile.ZipFile(zip_path) as z:
 for name in sorted(z.namelist()):
  if name.endswith('/') or not name.lower().endswith(('.usfm','.sfm','.txt')): continue
  text=z.read(name).decode('utf-8-sig','replace').splitlines(); book=None; chapter=None
  for line in text:
   m=book_re.match(line)
   if m: book=m.group(1); continue
   m=chapter_re.match(line)
   if m: chapter=int(m.group(1)); continue
   m=verse_re.match(line)
   if m and book and chapter:
    vn=int(re.match(r'\d+',m.group(1)).group()); t=m.group(2)
    t=notes.sub('',t); t=inline.sub('',t); t=re.sub(r"\\[a-z0-9+*]+\s?",'',t); t=re.sub(r'\s+',' ',t).strip()
    if t: rows.append({'book':book,'chapter':chapter,'verse':vn,'text':t})
out=root/'app/src/main/assets/bibles'/f'{version}.json';out.write_text(json.dumps(rows,ensure_ascii=False,separators=(',',':')),encoding='utf-8')
print(f"Generado {out} con {len(rows):,} versículos")
