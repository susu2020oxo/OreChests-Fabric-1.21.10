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
# Diamond block dominates the surface; the original pixels are a subtle relief layer.
# Keep every original alpha pixel and image dimension so leaves, item silhouettes,
# model UV layouts and animation frame metadata remain valid.
from PIL import ImageOps,ImageChops
original_diamond=Image.open(io.BytesIO(z.read('assets/minecraft/textures/block/diamond_block.png'))).convert('RGB')
def make_diamond_tile():
 tile=original_diamond.copy();pixels=[]
 for y in range(16):
  for x in range(16):
   r,g,b=tile.getpixel((x,y))
   # A saturated cyan body, pale mint facets, and distinct inset block edges.
   v=(r*.299+g*.587+b*.114)/255
   t=max(0,min(1,(v-.43)/.49))
   color=(int(6+150*t),int(140+112*t),int(151+100*t))
   if x==0 or y==0:color=(109,248,238)
   if x==15 or y==15:color=(6,133,146)
   # Two stepped specular facets, matching vanilla pixel-art resolution.
   if 8<=x+y<=9 and 2<=x<=12 and 2<=y<=12:
    color=tuple(round(c*.57+h*.43) for c,h in zip(color,(201,255,248)))
   if 18<=x+y<=19 and 3<=x<=12 and 3<=y<=12:
    color=tuple(round(c*.75+h*.25) for c,h in zip(color,(161,255,248)))
   pixels.append(color)
 tile.putdata(pixels);return tile
DIAMOND_TILE=make_diamond_tile()
CYAN_LUT=[]
for channel in range(3):
 for value in range(256):
  t=(value/255)**.86
  CYAN_LUT.append(round((4,43,54)[channel]+t*(174,212,198)[channel]))
def recolor(data,name):
 im=Image.open(io.BytesIO(data)).convert('RGBA');w,h=im.size
 if name.endswith('/entity/shadow.png'):return data
 luminance=ImageOps.grayscale(im)
 original_cyan=Image.merge('RGB',(luminance,luminance,luminance)).point(CYAN_LUT)
 entity='/entity/' in name and not any(x in name for x in ('/boat/','/chest/','/signs/','/decorated_pot/'))
 tile=DIAMOND_TILE.resize((8,8),Image.Resampling.NEAREST) if entity else DIAMOND_TILE
 tiled=Image.new('RGB',im.size)
 for y in range(0,h,tile.height):
  for x in range(0,w,tile.width):tiled.paste(tile,(x,y))
 # Diamond facets remain the main surface on blocks/items; slightly more facial relief on mobs.
 out=Image.blend(tiled,original_cyan,.28 if entity else .24)
 if not entity:
  relief=luminance.point(lambda v:max(0,min(85,(110-v))))
  out=Image.composite(original_cyan,out,relief)
 if entity:
  # Eyes and mouth retain enough dark relief to identify the creature.
  dark=luminance.point(lambda v:max(0,min(120,(64-v)*2)))
  out=Image.composite(original_cyan,out,dark)
 if name.endswith('/block/diamond_block.png'):out=DIAMOND_TILE.copy()
 out.putalpha(im.getchannel('A'))
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
for lang,data in langs.items():js('assets/diamondworld/lang/'+lang+'.json',data)
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
