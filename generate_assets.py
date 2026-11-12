"""Generate resources from an official 1.21.10 client jar. Requires Pillow.
Usage: python generate_assets.py /path/to/1.21.10.jar
Original game jar is not redistributed.
"""
import sys,zipfile,json,io,urllib.request
from pathlib import Path
from PIL import Image,ImageDraw
z=zipfile.ZipFile(sys.argv[1]);root=Path(__file__).parent/'src/main/resources'
def write(path,data):
 p=root/path;p.parent.mkdir(parents=True,exist_ok=True);p.write_bytes(data)
def js(path,data):write(path,json.dumps(data,ensure_ascii=False,indent=2).encode())
# Retain each source texture's complete relief and layout. The material palette
# comes ONLY from the unmodified vanilla diamond block (no invented cyan shades).
# The faint facet signal uses vanilla diamond interior pixels, never a repeated
# outer frame, so natural/rotated textures cannot create doubled tile borders.
from PIL import ImageOps,ImageChops
DIAMOND_PATH='assets/minecraft/textures/block/diamond_block.png'
DIAMOND_SOURCE=z.read(DIAMOND_PATH)
DIAMOND=Image.open(io.BytesIO(DIAMOND_SOURCE)).convert('RGB')
DIAMOND_PALETTE=sorted(set(DIAMOND.getdata()),key=lambda c:c[0]*.299+c[1]*.587+c[2]*.114)
PALETTE_LUT=[]
for channel in range(3):
 for value in range(256):
  PALETTE_LUT.append(DIAMOND_PALETTE[min(len(DIAMOND_PALETTE)-1,round(value*(len(DIAMOND_PALETTE)-1)/255))][channel])
DIAMOND_LUMA=ImageOps.grayscale(DIAMOND)
DLO,DHI=DIAMOND_LUMA.getextrema()
# Zero material modulation at the outer pixel ring prevents a stamped block frame.
FACET=Image.new('L',(16,16),128)
for y in range(1,15):
 for x in range(1,15):FACET.putpixel((x,y),round((DIAMOND_LUMA.getpixel((x,y))-DLO)*255/(DHI-DLO)))

def vanilla_diamond_asset(name):
 path=name.removeprefix('assets/minecraft/textures/')
 return (path in ('block/diamond_block.png','item/diamond.png','entity/shadow.png')
  or (path.startswith('item/diamond_') and path.endswith('.png'))
  or (path.startswith(('entity/equipment/','equipment/')) and path.endswith('/diamond.png')))

def pattern_material(data,name,contrast_reference=None):
 im=Image.open(io.BytesIO(data)).convert('RGBA');w,h=im.size
 luma=ImageOps.grayscale(im)
 # Normalize the source's own contrast, rather than replacing its bark, mortar,
 # grain, face, ore clusters or tool silhouette with a common diamond tile.
 reference=Image.open(io.BytesIO(contrast_reference)).convert('RGBA') if contrast_reference else im
 histogram=ImageOps.grayscale(reference).histogram(mask=reference.getchannel('A'))
 populated=[i for i,n in enumerate(histogram) if n]
 lo=populated[0] if populated else 0;hi=populated[-1] if populated else 255
 if hi>lo:pattern=luma.point([round(max(0,min(255,(v-lo)*255/(hi-lo)))) for v in range(256)])
 else:pattern=Image.new('L',im.size,255 if lo>220 else 128)
 facets=Image.new('L',im.size)
 for y in range(0,h,16):
  for x in range(0,w,16):facets.paste(FACET,(x,y))
 # 92% source relief; only 8% of the untouched diamond's native highlight signal.
 relief=Image.blend(pattern,facets,.08)
 rgb=Image.merge('RGB',(relief,relief,relief)).point(PALETTE_LUT)
 rgb.putalpha(im.getchannel('A'));return rgb

def ore_host(name):
 short=name.rsplit('/',1)[-1]
 if not (name.startswith('assets/minecraft/textures/block/') and short.endswith('_ore.png')):return None
 host='netherrack' if short.startswith('nether_') else 'deepslate' if short.startswith('deepslate_') else 'stone'
 return 'assets/minecraft/textures/block/'+host+'.png'

def recolor(data,name):
 if vanilla_diamond_asset(name):return data
 # Ancient debris is entirely mineral rather than ore inclusions in a rock host.
 if name in ('assets/minecraft/textures/block/ancient_debris_side.png','assets/minecraft/textures/block/ancient_debris_top.png'):return data
 host=ore_host(name)
 if host:
  source=Image.open(io.BytesIO(data)).convert('RGBA')
  raw_host=z.read(host);base=Image.open(io.BytesIO(raw_host)).convert('RGBA')
  out=pattern_material(data,name,raw_host)
  # Ores rearrange ordinary host noise, so a positional comparison would keep
  # unwanted gray rock pixels. Native-host palette exclusion identifies just
  # mineral/shadow pigments; preserve those original pixels byte-for-byte.
  mask=Image.new('L',source.size)
  host_palette=set(base.convert('RGB').getdata())
  mask.putdata([255 if a[:3] not in host_palette else 0 for a in source.getdata()])
  out=Image.composite(source,out,mask)
 else:out=pattern_material(data,name)
 buf=io.BytesIO();out.save(buf,format='PNG');return buf.getvalue()
count=0
for n in z.namelist():
 if n.startswith('assets/minecraft/textures/'):
  part=n.split('/')[3]
  if part in ('block','item','entity','equipment','painting','particle','environment'):
   if n.endswith('.png'):
    write(n,recolor(z.read(n),n))
    count+=1
   elif n.endswith('.mcmeta'):write(n,z.read(n))
# Vanilla grass randomly rotates Y by 90 degrees. Keep one orientation so native
# diamond highlights align across the floor instead of crisscrossing at seams.
js('assets/minecraft/blockstates/grass_block.json',{'variants':{
 'snowy=false':{'model':'minecraft:block/grass_block'},
 'snowy=true':{'model':'minecraft:block/grass_block_snow'}}})
# White maps prevent the grass/leaves biome tint from turning cyan back to green.
for name in ('grass','foliage','dry_foliage'):
 im=Image.new('RGB',(256,256),'white');buf=io.BytesIO();im.save(buf,format='PNG');write('assets/minecraft/textures/colormap/'+name+'.png',buf.getvalue())
langfile=Path(__file__).with_name('zh_cn.vanilla.json')
if not langfile.exists():
 manifest=json.load(urllib.request.urlopen('https://piston-meta.mojang.com/mc/game/version_manifest_v2.json'))
 version=next(v for v in manifest['versions'] if v['id']=='1.21.10')
 meta=json.load(urllib.request.urlopen(version['url']))
 index=json.load(urllib.request.urlopen(meta['assetIndex']['url']))
 digest=index['objects']['minecraft/lang/zh_cn.json']['hash']
 urllib.request.urlretrieve('https://resources.download.minecraft.net/'+digest[:2]+'/'+digest,langfile)
zh=json.loads(langfile.read_text());en=json.loads(z.read('assets/minecraft/lang/en_us.json'))
foods=['beef','cooked_beef','porkchop','cooked_porkchop','mutton','cooked_mutton','chicken','cooked_chicken','rabbit','cooked_rabbit','cod','cooked_cod','salmon','cooked_salmon','tropical_fish','pufferfish','rotten_flesh','spider_eye']
langs={'zh_cn':{'item.diamondworld.diamond_fragment':'钻石碎片','itemGroup.diamondworld':'钻石世界'},'en_us':{'item.diamondworld.diamond_fragment':'Diamond Fragment','itemGroup.diamondworld':'Diamond World'}}
def model(name,texture):
 js('assets/diamondworld/items/'+name+'.json',{'model':{'type':'minecraft:model','model':'diamondworld:item/'+name}})
 js('assets/diamondworld/models/item/'+name+'.json',{'parent':'minecraft:item/generated','textures':{'layer0':texture}})
model('diamond_fragment','diamondworld:item/diamond_fragment')
im=Image.new('RGBA',(16,16));d=ImageDraw.Draw(im);d.polygon([(8,2),(12,5),(10,11),(6,14),(3,9),(5,4)],fill=(16,177,190),outline=(4,85,110));d.line([(8,3),(6,8),(7,12)],fill=(180,255,249),width=2);buf=io.BytesIO();im.save(buf,format='PNG');write('assets/diamondworld/textures/item/diamond_fragment.png',buf.getvalue())
for f in foods:
 name='diamond_'+f;model(name,'minecraft:item/'+f)
 langs['zh_cn']['item.diamondworld.'+name]='钻石'+zh.get('item.minecraft.'+f,f)
 langs['en_us']['item.diamondworld.'+name]='Diamond '+en.get('item.minecraft.'+f,f)
# One distinct edible crystal per vanilla mob; runtime only awards these for MobEntity deaths.
mobs=[]
for k in en:
 if k.startswith('entity.minecraft.') and k.count('.')==2:
  name=k.split('.')[-1];mobs.append(name);item=name+'_essence';model(item,'minecraft:item/diamond')
  langs['zh_cn']['item.diamondworld.'+item]='钻石'+zh.get(k,name)+'精华'
  langs['en_us']['item.diamondworld.'+item]='Diamond '+en[k]+' Essence'
for lang,data in langs.items():
 path='assets/diamondworld/lang/'+lang+'.json'
 previous=json.loads((root/path).read_text()) if (root/path).exists() else {}
 previous.update(data);js(path,previous)
# Override only food entries; keep looting, conditions and essential progression drops.
def replace(obj):
 if isinstance(obj,dict):
  if obj.get('type')=='minecraft:item' and obj.get('name','').removeprefix('minecraft:') in foods:
   obj['name']='diamondworld:diamond_'+obj['name'].removeprefix('minecraft:')
  for value in obj.values():replace(value)
 elif isinstance(obj,list):
  for value in obj:replace(value)
loot_count=0
for n in z.namelist():
 if n.startswith('data/minecraft/loot_table/entities/') and n.endswith('.json'):
  data=json.loads(z.read(n));before=json.dumps(data);replace(data)
  if json.dumps(data)!=before:js(n,data);loot_count+=1
js('data/minecraft/loot_table/blocks/grass_block.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':'diamondworld:diamond_fragment'}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
recipe={'type':'minecraft:crafting_shapeless','category':'misc','ingredients':['diamondworld:diamond_fragment']*3,'result':{'id':'minecraft:diamond','count':1}}
js('data/diamondworld/recipe/diamond_from_fragments.json',recipe)
js('data/diamondworld/advancement/recipes/diamond_from_fragments.json',{'parent':'minecraft:recipes/root','criteria':{'has_fragment':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['diamondworld:diamond_fragment']}]}}},'requirements':[['has_fragment']],'rewards':{'recipes':['diamondworld:diamond_from_fragments']}})
for raw,cooked in [('beef','cooked_beef'),('porkchop','cooked_porkchop'),('mutton','cooked_mutton'),('chicken','cooked_chicken'),('rabbit','cooked_rabbit'),('cod','cooked_cod'),('salmon','cooked_salmon')]:
 for typ,time in [('smelting',200),('smoking',100),('campfire_cooking',600)]:
  js('data/diamondworld/recipe/'+raw+'_'+typ+'.json',{'type':'minecraft:'+typ,'category':'food','ingredient':'diamondworld:diamond_'+raw,'result':{'id':'diamondworld:diamond_'+cooked},'experience':.35,'cookingtime':time})
Path(__file__).with_name('generated-mobs.json').write_text(json.dumps(mobs))
print(f'Generated {count} recolored textures, {loot_count} food loot tables, {len(mobs)} species entries.')

for n in z.namelist():
 if n.startswith('assets/minecraft/items/') and n.endswith('.json'):
  data=json.loads(z.read(n));changed=[False]
  def untint(o):
   if isinstance(o,dict):
    if 'tints' in o:
     o['tints']=[{'type':'minecraft:constant','value':-1} for _ in o['tints']];changed[0]=True
    for v in o.values():untint(v)
   elif isinstance(o,list):
    for v in o:untint(v)
  untint(data)
  if changed[0]:js(n,data)

# Essence portraits intentionally retain recognizable original face details and a
# small diamond accent. Run last so the generic translation/model pass above does
# not replace the mob-specific portrait textures.
import subprocess
subprocess.run([sys.executable,str(Path(__file__).with_name('generate_essence_icons.py')),sys.argv[1]],check=True)


# Ability effect names and HUD sprites; keep this after portraits so regeneration is complete.
effect_names={
 'flight':('钻石飞翼','Diamond Flight','slow_falling'),
 'climbing':('蜘蛛攀墙','Spider Climbing','jump_boost'),
 'blink':('末影闪现','Ender Blink','invisibility'),
 'thorns':('守卫反伤','Guardian Thorns','resistance'),
 'fire_touch':('烈焰附身','Blazing Touch','fire_resistance'),
 'venom_touch':('毒刺','Venomous Touch','poison'),
 'wither_touch':('凋零之触','Wither Touch','wither'),
 'creeper_power':('苦力怕蓄爆','Creeper Charge','strength'),
 'echo':('回声探测','Echo Sense','night_vision'),
 'prospecting':('嗅探寻矿','Sniffer Prospecting','luck'),
 'rooted_guard':('嘎枝扎根','Creaking Roots','resistance'),
 'spit':('羊驼吐息','Llama Spit','strength'),
 'snow_throw':('雪傀儡投雪','Snow Golem Throw','slow_falling'),
 'ram':('山羊冲撞','Goat Ram','strength'),
 'feline_ward':('猫科威慑','Feline Ward','night_vision')}
for lang,index in [('zh_cn',0),('en_us',1)]:
 path='assets/diamondworld/lang/'+lang+'.json'
 data=json.loads((root/path).read_text())
 for key,names in effect_names.items():data['effect.diamondworld.'+key]=names[index]
 js(path,data)
for key,names in effect_names.items():
 write('assets/diamondworld/textures/mob_effect/'+key+'.png',z.read('assets/minecraft/textures/mob_effect/'+names[2]+'.png'))
