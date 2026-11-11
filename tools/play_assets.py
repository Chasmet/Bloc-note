from pathlib import Path
from PIL import Image, ImageDraw, ImageFilter, ImageFont
import math

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "app/src/main/res/drawable-nodpi/ic_note_brand.jpg"
OUT = ROOT / "play-store/assets"
OUT.mkdir(parents=True, exist_ok=True)
assert SOURCE.is_file(), "Logo natif absent"
logo = Image.open(SOURCE).convert("RGB")
def fit_logo(width):
    return logo.resize((width,width), Image.Resampling.LANCZOS)

# L'icône reprend exactement le logo déjà présent dans l'application.
icon = fit_logo(512)
icon.save(OUT / "sync30-blocnote-icon-512.png", optimize=True)

# Une seule image marketing distincte des captures Android (ne simule pas l'interface).
w,h=1024,500
banner = Image.new("RGB",(w,h))
pixels=banner.load()
for y in range(h):
    for x in range(w):
        light=max(0, 1-((x-755)**2+(y-215)**2)**0.5/670)
        line=max(0, (x-0.6*y)/w)
        pixels[x,y]=(int(20+36*light+14*line), int(11+21*light+7*line), int(9+13*light+3*line))
draw=ImageDraw.Draw(banner)
for i in range(7):
    pad=16+i*16
    draw.rounded_rectangle((pad,pad,w-pad,h-pad),radius=38,outline=(58+i*5,31+i*3,18+i*2),width=1)
for dx in range(-420,400,84):
    draw.line((700+dx,0,1160+dx,500),fill=(72,43,25),width=2)
fbase="/usr/share/fonts/truetype/dejavu/DejaVuSans"
title_font=ImageFont.truetype(fbase+"-Bold.ttf",53)
body_font=ImageFont.truetype(fbase+".ttf",26)
small_font=ImageFont.truetype(fbase+"-Bold.ttf",19)
draw.rounded_rectangle((46,64,172,103),radius=19,fill=(100,57,26))
draw.text((67,72),"NOTES",font=small_font,fill=(252,201,135))
draw.text((48,146),"Sync 30",font=title_font,fill=(255,241,223))
draw.text((48,217),"Bloc-note",font=title_font,fill=(237,161,74))
draw.text((50,309),"Toutes vos idées, bien organisées.",font=body_font,fill=(245,215,187))
draw.rounded_rectangle((47,398,469,436),radius=19,fill=(74,43,27),outline=(181,114,51),width=2)
draw.text((67,403),"Notes • Dossiers • Favoris",font=ImageFont.truetype(fbase+".ttf",20),fill=(254,208,163))
size=302
back=Image.new("RGBA",(size+24,size+24),(0,0,0,0))
d=ImageDraw.Draw(back)
d.rounded_rectangle((0,0,size+23,size+23),radius=54,fill=(6,4,3,255))
d.rounded_rectangle((8,8,size+15,size+15),radius=46,outline=(196,111,46),width=4)
banner.paste(back.convert("RGB"),(653,89))
logo_big=fit_logo(size)
mask=Image.new("L",(size,size),0)
md=ImageDraw.Draw(mask);md.rounded_rectangle((0,0,size,size),radius=38,fill=255)
banner.paste(logo_big,(665,101),mask)
banner.save(OUT/"sync30-blocnote-feature-1024x500.jpg",quality=96,subsampling=0)
print("Generated",[(p.name,Image.open(p).size) for p in OUT.iterdir() if p.suffix.lower() in (".png",".jpg")])
