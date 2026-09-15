from pathlib import Path
import json
root=Path(__file__).resolve().parents[1]
books=json.loads((root/'app/src/main/assets/catalog/books.json').read_text(encoding='utf-8'))
assert len(books)==66
entries=json.loads((root/'app/src/main/assets/plans/entries.json').read_text(encoding='utf-8'))
assert len([x for x in entries if x['planId']=='year'])==365
assert len([x for x in entries if x['planId']=='chrono'])==365
for f in ['rv1909.json','web.json']:
 json.loads((root/'app/src/main/assets/bibles'/f).read_text(encoding='utf-8'))
print('OK: estructura, 66 libros, 2 planes x 365 días y JSON bíblico válidos.')
