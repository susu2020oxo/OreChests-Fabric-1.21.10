"""Recognizable 32px mob portrait thumbnails from vanilla head UVs.
The natural face is retained for recognition, with a small diamond crystal accent.
Run: python generate_essence_icons.py /path/to/1.21.10.jar
"""
import io,json,sys,zipfile
from pathlib import Path
from PIL import Image,ImageDraw,ImageEnhance
ROOT=Path(__file__).parent/'src/main/resources/assets/diamondworld'
Z=zipfile.ZipFile(sys.argv[1])
# (entity texture, front-face UV rectangle, silhouette additions)
F={}
def group(names,tex,box=(8,8,16,16),shape='square'):
 for n in names.split():F[n]=(tex,box,shape)
group('zombie giant','zombie/zombie')
group('husk','zombie/husk');group('drowned','zombie/drowned')
group('skeleton','skeleton/skeleton');group('stray','skeleton/stray');group('bogged','skeleton/bogged',shape='mushroom')
group('wither_skeleton','skeleton/wither_skeleton');group('creeper','creeper/creeper')
group('enderman','enderman/enderman');group('blaze','blaze',shape='flame')
group('cow','cow/temperate_cow',(6,6,14,14),'horns');group('mooshroom','cow/red_mooshroom',(6,6,14,14),'mushroom')
group('pig','pig/temperate_pig',(8,8,16,16),'pig')
group('sheep','sheep/sheep',(8,8,14,14),'wool')
group('chicken','chicken/temperate_chicken',(3,3,7,9),'chicken')
group('wolf','wolf/wolf',(4,4,10,10),'dog')
group('cat','cat/tabby',(4,4,9,8),'cat');group('ocelot','cat/ocelot',(4,4,9,8),'cat')
group('fox','fox/fox',(7,11,15,17),'fox')
group('rabbit','rabbit/brown',(37,5,42,9),'rabbit');group('killer_bunny','rabbit/caerbannog',(37,5,42,9),'rabbit')
group('bat','bat',(0,6,6,12),'bat')
group('spider','spider/spider',(40,12,48,20),'spider');group('cave_spider','spider/cave_spider',(40,12,48,20),'spider')
group('zombie_villager','zombie_villager/zombie_villager',(8,8,16,18),'nose')
group('villager','villager/villager',(8,8,16,18),'nose');group('wandering_trader','wandering_trader',(8,8,16,18),'hood')
group('pillager','illager/pillager',(8,8,16,20),'nose');group('vindicator','illager/vindicator',(8,8,16,20),'nose')
group('evoker','illager/evoker',(8,8,16,20),'magic');group('illusioner','illager/illusioner',(8,8,16,20),'magic')
group('witch','witch',(8,8,16,18),'witch')
group('piglin','piglin/piglin',(8,8,18,16),'piglin');group('piglin_brute','piglin/piglin_brute',(8,8,18,16),'piglin')
group('zombified_piglin','piglin/zombified_piglin',(8,8,18,16),'piglin')
group('hoglin','hoglin/hoglin',(14,14,30,28),'tusks');group('zoglin','hoglin/zoglin',(14,14,30,28),'tusks')
group('slime','slime/slime',(8,8,16,16),'slime');group('magma_cube','slime/magmacube',(8,8,16,16),'magma')
group('ghast','ghast/ghast',(32,32,64,64),'tentacles');group('happy_ghast','ghast/happy_ghast',(32,32,64,64),'tentacles')
group('squid','squid/squid',(12,12,24,28),'tentacles');group('glow_squid','squid/glow_squid',(12,12,24,28),'tentacles')
group('iron_golem','iron_golem/iron_golem',(8,8,16,18),'nose')
group('snow_golem','snow_golem',(8,8,16,16),'snow')
group('copper_golem','copper_golem/copper_golem',(8,8,16,16),'antenna')
group('polar_bear','bear/polarbear',(7,7,14,14),'bear')
group('panda','panda/panda',(13,13,26,23),'bear')
group('horse','horse/horse_brown',(0,16,8,24),'horse');group('donkey','horse/donkey',(0,16,8,24),'donkey');group('mule','horse/mule',(0,16,8,24),'donkey')
group('skeleton_horse','horse/horse_skeleton',(0,16,8,24),'horse');group('zombie_horse','horse/horse_zombie',(0,16,8,24),'horse')
group('llama','llama/creamy',(6,6,10,10),'llama');group('trader_llama','llama/white',(6,6,10,10),'trader')
group('camel','camel/camel',(33,2,40,10),'camel')
group('goat','goat/goat',(34,58,39,63),'goat')
group('bee','bee/bee',(7,7,14,14),'bee')
group('allay','allay/allay',(5,5,10,10),'fairy');group('vex','illager/vex',(5,5,10,10),'fairy')
group('breeze','breeze/breeze',(4,4,12,12),'wind')
group('warden','warden/warden',(16,16,32,32),'warden')
group('creaking creaking_transient','creaking/creaking',(8,8,16,18),'creaking')
group('sniffer','sniffer/sniffer',(8,8,18,22),'sniffer')
group('armadillo','armadillo',(8,8,16,14),'armadillo')
group('axolotl','axolotl/axolotl_lucy',(2,2,10,7),'axolotl')
group('frog','frog/temperate_frog',(4,2,12,7),'frog');group('tadpole','tadpole/tadpole',(3,3,6,5),'tadpole')
group('turtle','turtle/big_sea_turtle',(3,3,9,8),'turtle')
group('dolphin','dolphin',(0,0,8,7),'dolphin')
group('guardian','guardian',(16,16,28,28),'guardian');group('elder_guardian','guardian_elder',(16,16,28,28),'guardian')
group('cod','fish/cod',(0,0,6,4),'fish');group('salmon','fish/salmon',(0,0,6,4),'fish')
group('tropical_fish','fish/tropical_a',(0,0,6,4),'tropical');group('pufferfish','fish/pufferfish',(4,4,12,12),'puffer')
group('strider','strider/strider',(16,16,32,32),'strider')
group('silverfish','silverfish',(0,0,6,4),'silverfish');group('endermite','endermite',(0,0,6,4),'endermite')
group('phantom','phantom',(4,4,11,7),'phantom')
group('ravager','illager/ravager',(8,16,24,28),'ravager')
group('wither','wither/wither',(8,8,16,16),'wither')
group('ender_dragon','enderdragon/dragon',(80,48,96,64),'dragon')
group('parrot','parrot/parrot_red_blue',(2,2,5,7),'parrot')

group('shulker','shulker/shulker',(16,28,24,36),'shulker')
group('mannequin','player/wide/steve',(8,8,16,16),'square')

CYAN=(109,255,246,255);DARK=(6,73,89,255)
def color(im):
 # Minimal cyan sheen while preserving the species' natural face colors.
 p=[]
 for r,g,b,a in im.getdata():p.append((int(r*.89+10),int(g*.89+19),int(b*.89+20),a))
 im.putdata(p);return im

def make(name,tex,box,shape):
 source=Image.open(io.BytesIO(Z.read('assets/minecraft/textures/entity/'+tex+'.png'))).convert('RGBA')
 face=source.crop(box);im=Image.new('RGBA',(32,32));d=ImageDraw.Draw(im)
 # Portrait silhouettes are intentionally different: ears, horns, wings, gills and tentacles.
 if shape in ['bat','phantom']:
  wing=(72,46,62,255) if shape=='bat' else (56,74,109,255)
  d.polygon([(1,10),(7,12),(10,9),(12,15),(21,15),(23,9),(26,12),(31,10),(28,23),(25,20),(23,24),(9,24),(7,20),(4,23)],fill=wing,outline=DARK)
 if shape in ['fairy','bee']:
  d.polygon([(1,10),(5,8),(11,14),(11,24),(4,21)],fill=(185,242,245,255),outline=DARK)
  d.polygon([(31,10),(27,8),(21,14),(21,24),(28,21)],fill=(185,242,245,255),outline=DARK)
 if shape=='tentacles':
  for x in [8,13,19,24]:d.line([(x,20),(x,27),(x-2,29)],fill=(88,119,137,255),width=3)
 if shape=='spider':
  for y in [13,18,23]:
   d.line([(10,y),(4,y-3),(1,y)],fill=(51,44,45,255),width=2);d.line([(22,y),(28,y-3),(31,y)],fill=(51,44,45,255),width=2)
 if shape=='wool':d.rounded_rectangle((3,5,29,28),radius=4,fill=(237,238,227,255),outline=(135,178,181,255),width=2)
 if shape in ['horns','goat','ravager','tusks']:
  d.polygon([(4,12),(3,3),(8,6),(10,14)],fill=(233,227,199,255),outline=DARK)
  d.polygon([(28,12),(29,3),(24,6),(22,14)],fill=(233,227,199,255),outline=DARK)
 if shape in ['cat','fox','dog','bear','pig','piglin']:
  ear=(106,80,59,255) if shape not in ['pig','piglin'] else (231,162,154,255)
  d.polygon([(5,14),(5,3),(12,8),(12,15)],fill=ear,outline=DARK);d.polygon([(27,14),(27,3),(20,8),(20,15)],fill=ear,outline=DARK)
 if shape in ['rabbit','horse','donkey','llama','trader','camel']:
  ear=(169,136,101,255)
  d.rectangle((7,1,11,14),fill=ear,outline=DARK);d.rectangle((21,1,25,14),fill=ear,outline=DARK)
 if shape in ['warden','axolotl']:
  c=(62,184,185,255) if shape=='warden' else (239,121,181,255)
  for x,sgn in [(6,-1),(26,1)]:
   d.line([(x,17),(x+sgn*4,13),(x+sgn*4,6)],fill=c,width=3)
   if shape=='axolotl':
    for y in [8,12,16]:d.line([(x,y+5),(x+sgn*4,y)],fill=c,width=2)
 if shape in ['guardian','puffer']:
  c=(221,162,83,255)
  for x in [4,14,25]:d.polygon([(x,8),(x+2,2),(x+4,9)],fill=c,outline=DARK)
  d.polygon([(3,14),(0,17),(4,21)],fill=c);d.polygon([(28,14),(31,17),(27,21)],fill=c)
 if shape in ['fish','tropical','tadpole','silverfish','endermite','dolphin','turtle']:
  # Small side-profile thumbnails, with eyes and fins; source face supplies natural texture.
  c=source.resize((1,1)).getpixel((0,0));c=tuple(max(60,v) for v in c[:3])+(255,)
  if shape=='tropical':c=(237,155,56,255)
  if shape=='dolphin':c=(124,160,181,255)
  if shape=='turtle':c=(59,139,88,255)
  d.polygon([(1,10),(8,14),(8,21),(1,25)],fill=c,outline=DARK)
  d.rounded_rectangle((7,10,28,25),radius=5,fill=c,outline=DARK,width=2)
  d.ellipse((21,13,24,16),fill=(20,28,34,255));d.polygon([(12,10),(15,5),(20,11)],fill=c,outline=DARK)
  if shape=='turtle':
   d.ellipse((8,9,23,25),fill=(62,113,72,255),outline=(34,68,51,255),width=2)
   for x in [7,20]:d.rectangle((x,24,x+5,27),fill=c)
  if shape=='dolphin':d.rectangle((25,19,31,22),fill=c,outline=DARK)
  if shape in ['silverfish','endermite']:
   for x in [9,14,19,24]:d.line([(x,24),(x-2,28)],fill=c,width=2)
 else:
  # Face UV rendered at a crisp integer scale, centered with a one-pixel cyan edge.
  fw,fh=face.size;scale=min(22//max(1,fw),20//max(1,fh));scale=max(1,scale)
  face=color(face).resize((fw*scale,fh*scale),Image.Resampling.NEAREST)
  if face.width<14 or face.height<12:face=face.resize((max(16,face.width),max(16,face.height)),Image.Resampling.NEAREST)
  if face.width>24 or face.height>22:face.thumbnail((24,22),Image.Resampling.NEAREST)
  x=(32-face.width)//2;y=max(7,(32-face.height)//2)
  d.rectangle((x-1,y-1,x+face.width,y+face.height),fill=DARK)
  im.alpha_composite(face,(x,y));d=ImageDraw.Draw(im)
  if shape in ['pig','piglin']:
   d.rectangle((11,20,22,25),fill=(226,156,145,255),outline=(140,91,91,255));d.point((14,22),fill=(78,56,59,255));d.point((19,22),fill=(78,56,59,255))
  if shape in ['nose','hood','witch','magic']:
   d.rectangle((14,18,18,26),fill=(157,132,104,255),outline=(78,77,67,255))
  if shape=='chicken':
   d.rectangle((12,19,20,23),fill=(239,191,59,255));d.rectangle((14,24,18,27),fill=(175,45,50,255));d.rectangle((14,2,18,7),fill=(192,48,52,255))
  if shape in ['witch','magic']:
   d.rectangle((4,7,28,10),fill=(52,35,71,255));d.polygon([(10,7),(11,1),(20,1),(23,7)],fill=(65,43,84,255));d.rectangle((12,5,21,6),fill=(153,173,81,255))
  if shape=='mushroom':
   d.rectangle((8,2,9,7),fill=(227,214,181,255));d.rectangle((4,1,12,4),fill=(188,47,46,255));d.point((7,2),fill='white');d.point((11,3),fill='white')
  if shape=='flame':
   for x,y in [(3,17),(6,25),(25,15),(25,26)]:d.rectangle((x,y,x+2,y+5),fill=(241,179,36,255));d.point((x,y),fill=(255,239,153,255))
  if shape=='antenna':d.line([(15,7),(15,2),(19,2)],fill=(153,93,58,255),width=2)
  if shape=='wind':
   d.arc((1,20,30,30),0,300,fill=(192,238,244,255),width=2);d.arc((2,2,29,15),170,350,fill=(192,238,244,255),width=2)
  if shape=='snow':
   d.rectangle((10,13,13,16),fill=(37,36,31,255));d.rectangle((20,13,23,16),fill=(37,36,31,255));d.rectangle((14,19,20,21),fill=(210,103,37,255))
  if shape=='wither':
   little=face.resize((9,9),Image.Resampling.NEAREST);im.alpha_composite(little,(0,15));im.alpha_composite(little,(23,15))
  if shape=='dragon':
   d.rectangle((6,11,26,24),fill=(37,34,44,255));d.rectangle((4,4,6,13),fill=(110,114,126,255));d.rectangle((25,4,27,13),fill=(110,114,126,255));d.rectangle((9,13,12,15),fill=(210,89,255,255));d.rectangle((21,13,24,15),fill=(210,89,255,255));d.rectangle((11,22,22,26),fill=(45,43,52,255))
  if shape=='warden':
   d.rectangle((10,19,22,23),fill=(28,53,51,255));d.line((11,20,21,20),fill=(114,146,130,255),width=1)
  if name=='piglin_brute':d.rectangle((6,16,9,22),fill=(227,183,55,255))
  if name=='happy_ghast':
   d.line([(11,19),(13,21),(19,21),(21,19)],fill=(99,109,122,255),width=1)
   d.rectangle((8,10,23,12),fill=(127,165,188,255))
  if shape=='creaking':d.rectangle((10,14,13,15),fill=(255,174,58,255));d.rectangle((20,13,23,14),fill=(255,174,58,255))
  if shape in ['llama','trader']:
   d.rectangle((8,10,24,25),fill=(227,218,184,255));d.rectangle((13,21,20,25),fill=(155,132,105,255))
  if shape in ['horse','donkey','llama','trader','camel']:
   d.rectangle((9,14,11,16),fill=(22,24,28,255));d.rectangle((21,14,23,16),fill=(22,24,28,255))
   d.rectangle((12,22,21,26),fill=(179,154,124,255));d.point((14,24),fill=(54,43,36,255));d.point((19,24),fill=(54,43,36,255))
  if shape=='fairy':
   d.rectangle((11,13,13,15),fill=(233,255,255,255));d.rectangle((19,13,21,15),fill=(233,255,255,255))
  if shape=='trader':
   d.rectangle((7,26,25,29),fill=(44,101,173,255));d.rectangle((7,27,9,29),fill=(229,186,75,255));d.rectangle((23,27,25,29),fill=(229,186,75,255))
  if shape=='rabbit':
   d.rectangle((11,14,12,16),fill=(26,25,24,255));d.rectangle((20,14,21,16),fill=(26,25,24,255));d.rectangle((15,20,18,21),fill=(210,153,151,255))
  if shape=='bat':
   d.rectangle((12,15,13,16),fill=(243,235,216,255));d.rectangle((19,15,20,16),fill=(243,235,216,255));d.polygon([(10,10),(10,4),(14,10)],fill=(89,53,61,255));d.polygon([(18,10),(22,4),(22,10)],fill=(89,53,61,255))
  if shape=='frog':
   d.rectangle((7,8,12,13),fill=(197,199,135,255));d.rectangle((20,8,25,13),fill=(197,199,135,255));d.rectangle((9,10,11,12),fill=(30,33,25,255));d.rectangle((21,10,23,12),fill=(30,33,25,255));d.line((9,23,23,23),fill=(84,65,45,255),width=2)
  if shape=='sniffer':
   d.rectangle((5,7,26,15),fill=(57,111,63,255));d.rectangle((6,15,25,24),fill=(172,67,44,255));d.rectangle((8,19,24,27),fill=(222,170,56,255));d.rectangle((10,21,13,24),fill=(91,65,38,255));d.rectangle((19,21,22,24),fill=(91,65,38,255));d.rectangle((8,15,11,16),fill=(29,27,26,255));d.rectangle((21,15,24,16),fill=(29,27,26,255))
  if shape=='armadillo':
   d.rectangle((11,17,13,19),fill=(35,27,25,255));d.rectangle((20,17,22,19),fill=(35,27,25,255));d.rectangle((13,23,20,26),fill=(154,113,99,255))
  if shape=='shulker':
   d.rectangle((4,7,28,14),fill=(119,78,126,255),outline=(68,43,77,255));d.rectangle((3,14,29,17),fill=(157,112,164,255));d.rectangle((4,24,28,27),fill=(119,78,126,255),outline=(68,43,77,255));d.rectangle((12,17,20,23),fill=(222,217,158,255));d.point((14,20),fill='black');d.point((18,20),fill='black')
  if shape=='ravager':
   d.rectangle((9,15,12,17),fill=(202,54,52,255));d.rectangle((20,15,23,17),fill=(202,54,52,255));d.rectangle((11,21,21,26),fill=(116,115,108,255));d.rectangle((13,23,14,24),fill=(49,48,46,255));d.rectangle((18,23,19,24),fill=(49,48,46,255))
  if shape=='parrot':d.polygon([(11,8),(14,0),(17,7)],fill=(221,43,47,255));d.rectangle((15,18,22,22),fill=(241,187,74,255))
 if shape in ['silverfish','endermite']:
  im=Image.new('RGBA',(32,32));d=ImageDraw.Draw(im)
  c=(132,145,150,255) if shape=='silverfish' else (108,79,129,255)
  for i in range(5):
   x=5+i*4;y=14-abs(i-2)
   d.rectangle((x,y,x+5,y+8),fill=c,outline=DARK)
   d.line([(x+2,y+8),(x,y+12)],fill=c,width=2)
  d.point((25,15),fill=(236,154,214,255));d.line([(4,15),(1,11)],fill=c,width=1)
 # A diamond sliver marks the item as essence without hiding the creature portrait.
 d=ImageDraw.Draw(im);d.polygon([(27,24),(31,27),(28,31),(24,28)],fill=(55,220,221,255),outline=DARK);d.line([(27,25),(25,28),(28,29)],fill=(215,255,249,255),width=1)
 return im

def main():
 out=ROOT/'textures/item';out.mkdir(parents=True,exist_ok=True)
 for name,(tex,box,shape) in F.items():
  im=make(name,tex,box,shape);im.save(out/(name+'_essence.png'))
  p=ROOT/'models/item'/(name+'_essence.json');p.parent.mkdir(exist_ok=True,parents=True)
  p.write_text(json.dumps({'parent':'minecraft:item/generated','textures':{'layer0':'diamondworld:item/'+name+'_essence'}},indent=2))
 cols=12;cell=96;sheet=Image.new('RGB',(cols*cell,((len(F)+cols-1)//cols)*104),(29,37,44));d=ImageDraw.Draw(sheet)
 for i,name in enumerate(F):
  x=i%cols*cell;y=i//cols*104;im=Image.open(out/(name+'_essence.png')).resize((80,80),Image.Resampling.NEAREST);sheet.paste(im,(x+8,y),im);d.text((x+2,y+82),name[:15],fill=(232,244,245))
 p=Path(__file__).parent/'previews/essence-icons-preview.png';p.parent.mkdir(parents=True,exist_ok=True);sheet.save(p)
 print('Generated',len(F),'recognizable mob portraits; preview:',p)
if __name__=='__main__':main()
