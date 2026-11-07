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
# Monotonic luminance mapping preserves every original pixel, alpha and animation frame.
palette=[(0,(3,39,49)),(.20,(4,82,92)),(.40,(7,132,142)),(.60,(23,184,188)),(.78,(72,222,218)),(1,(211,255,249))]
def recolor(data):
 im=Image.open(io.BytesIO(data)).convert('RGBA');out=[]
 for r,g,b,a in im.getdata():
  v=(r*.299+g*.587+b*.114)/255
  # A modest highlight lift gives the diamond-block cyan sheen without replacing texture.
  v=min(1,v**.86)
  for (lo,c1),(hi,c2) in zip(palette,palette[1:]):
   if v<=hi:
    t=(v-lo)/(hi-lo);out.append(tuple(round(x+(y-x)*t) for x,y in zip(c1,c2))+(a,));break
 im.putdata(out);buf=io.BytesIO();im.save(buf,format='PNG');return buf.getvalue()
count=0
for n in z.namelist():
 if n.startswith('assets/minecraft/textures/'):
  part=n.split('/')[3]
  if part in ('block','item','entity','equipment','painting','particle','environment'):
   if n.endswith('.png'):
    if not (root/n).exists():write(n,recolor(z.read(n)))
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
