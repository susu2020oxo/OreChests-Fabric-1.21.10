"""Validate generated source texture fidelity and write original/new contact sheets.
Usage: python validate_visual_assets.py /path/to/1.21.10.jar [preview-directory]
Requires Pillow only; does not start Minecraft or modify generated textures.
"""
import io,json,sys,zipfile,hashlib,math
from pathlib import Path
from PIL import Image,ImageDraw,ImageOps,ImageChops
project=Path(__file__).parent
root=project/'src/main/resources'
out=Path(sys.argv[2]) if len(sys.argv)>2 else project/'previews'
out.mkdir(parents=True,exist_ok=True)
z=zipfile.ZipFile(sys.argv[1])
prefix='assets/minecraft/textures/'
palette=set(Image.open(io.BytesIO(z.read(prefix+'block/diamond_block.png'))).convert('RGB').getdata())

def protected(name):
 p=name.removeprefix(prefix)
 return (p in ('block/diamond_block.png','item/diamond.png','entity/shadow.png')
  or (p.startswith('item/diamond_') and p.endswith('.png'))
  or (p.startswith(('entity/equipment/','equipment/')) and p.endswith('/diamond.png')))

def mineral(name):return name in (prefix+'block/ancient_debris_side.png',prefix+'block/ancient_debris_top.png')

def host_for(name):
 short=name.rsplit('/',1)[-1]
 if not(name.startswith(prefix+'block/') and short.endswith('_ore.png')):return None
 return prefix+'block/'+('netherrack' if short.startswith('nether_') else 'deepslate' if short.startswith('deepslate_') else 'stone')+'.png'

def load_original(name):return Image.open(io.BytesIO(z.read(name))).convert('RGBA')

def covariance_corr(xs,ys):
 n=len(xs);mx=sum(xs)/n;my=sum(ys)/n
 xx=sum((x-mx)**2 for x in xs);yy=sum((y-my)**2 for y in ys)
 return sum((x-mx)*(y-my) for x,y in zip(xs,ys))/(xx*yy)**.5 if xx*yy else 1.

names=[n for n in z.namelist() if n.startswith(prefix) and n.endswith('.png') and n.split('/')[3] in ('block','item','entity','equipment','painting','particle','environment')]
results={'texture_count':len(names),'alpha_and_dimensions':'PASS','untouched_vanilla_assets':[],'ores':{},'pattern_luminance_correlation':{}}
for name in names:
 a=load_original(name);b=Image.open(root/name).convert('RGBA')
 assert a.size==b.size,('dimensions',name)
 assert ImageChops.difference(a.getchannel('A'),b.getchannel('A')).getbbox() is None,('alpha',name)
 if protected(name) or mineral(name):
  assert (root/name).read_bytes()==z.read(name),('byte identity',name)
  results['untouched_vanilla_assets'].append(name)
 elif (host:=host_for(name)):
  base=load_original(host);host_palette=set(base.convert('RGB').getdata())
  minerals=0
  for source,generated in zip(a.getdata(),b.getdata()):
   if source[:3] not in host_palette:
    minerals+=1;assert source==generated,('ore pigment changed',name)
   else:assert generated[:3] in palette,('gray/non-diamond ore host pixel',name)
  results['ores'][name]={'exact_original_mineral_pixels':minerals,'host_pixels':a.width*a.height-minerals,'status':'PASS'}
 else:
  assert all(c[:3] in palette for c in b.getdata()),('outside exact vanilla diamond palette',name)
# Original source ordering is the dominant signal; measure representative block relief.
blocks=['grass_block_top','oak_log','oak_log_top','oak_planks','cobblestone','stone','stone_bricks','crafting_table_front','crafting_table_top','furnace_front','furnace_front_on','oak_leaves','dirt','bricks','sand','glass']
for short in blocks:
 name=prefix+'block/'+short+'.png';a=load_original(name);b=Image.open(root/name).convert('RGBA')
 xy=[(sum(p[i]*v for i,v in enumerate((.299,.587,.114))),sum(q[i]*v for i,v in enumerate((.299,.587,.114)))) for p,q in zip(a.getdata(),b.getdata()) if p[3]>0]
 results['pattern_luminance_correlation'][short]=round(covariance_corr([v[0] for v in xy],[v[1] for v in xy]),4)
 assert results['pattern_luminance_correlation'][short]>=.92,('source relief insufficient',short)
state=json.loads((root/'assets/minecraft/blockstates/grass_block.json').read_text())
assert all(isinstance(v,dict) and v.get('y',0)==0 for v in state['variants'].values())
results['grass_blockstate']='one fixed orientation; vanilla models and geometry; no frame stamped on texture'

BG=(24,33,43);FG=(235,246,250)
def contact(shorts,name):
 pairs_per_row=3;tile=112;pair_w=256;header=48;row_h=156
 sheet=Image.new('RGB',(pairs_per_row*pair_w,header+math.ceil(len(shorts)/pairs_per_row)*row_h),BG);d=ImageDraw.Draw(sheet)
 d.text((12,8),'VANILLA SOURCE  |  DIAMOND MATERIAL + ORIGINAL PATTERN',fill=FG)
 for i,short in enumerate(shorts):
  path=prefix+'block/'+short+'.png';a=load_original(path);b=Image.open(root/path).convert('RGBA')
  x=i%pairs_per_row*pair_w;y=header+i//pairs_per_row*row_h
  for j,im in enumerate((a,b)):
   crop=im.crop((0,0,im.width,min(im.width,im.height))).resize((tile,tile),Image.Resampling.NEAREST)
   sheet.paste(crop,(x+8+j*120,y),crop)
  d.text((x+8,y+116),short,fill=FG);d.text((x+8,y+132),'original              new',fill=(147,180,191))
 sheet.save(out/name)
contact(blocks,'v12-block-patterns.png')
ores=[n.rsplit('/',1)[-1].removesuffix('.png') for n in sorted(results['ores'])]
contact(ores+['ancient_debris_side','ancient_debris_top'],'v12-all-ores.png')
# Floor repetition uses exactly one texture per physical block, without added seams.
g=Image.open(root/(prefix+'block/grass_block_top.png')).convert('RGB').resize((96,96),Image.Resampling.NEAREST)
floor=Image.new('RGB',(576,576))
for y in range(6):
 for x in range(6):floor.paste(g,(x*96,y*96))
floor.save(out/'v12-grass-floor.png')
(out/'v12-visual-validation.json').write_text(json.dumps(results,indent=2))
print(json.dumps({'textures':len(names),'untouched_vanilla_assets':len(results['untouched_vanilla_assets']),'ores_with_exact_source_minerals':len(results['ores']),'minimum_pattern_correlation':min(results['pattern_luminance_correlation'].values()),'contact_sheets':str(out),'status':'PASS'},indent=2))
