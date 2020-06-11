
from PIL import Image

width = 1024
height = 1024

img_red = Image.open('path', 'r')
pix_val_red = list(img_red.getdata())

img_nir = Image.open('path', 'r')
pix_val_nir = list(img_nir.getdata())

pix_val_ndvi = []
for i in range(len(pix_val_red)):
    # Use this only if the image compression is not black and white:
    # red = (pix_val_red[i][0]+pix_val_red[i][1]+pix_val_red[i][2])/3/255
    # nir = (pix_val_nir[i][0]+pix_val_nir[i][1]+pix_val_nir[i][2])/3/255
    red = pix_val_red[i]/255
    nir = pix_val_nir[i]/255
    ndvi = (nir-red)/(nir+red)
    # r = int((-ndvi**2-2*ndvi+3)*255/4)
    # g = int((-ndvi**2+2*ndvi+3)*255/4)
    r = int(255/2*(1-ndvi))
    g = int(255/2*(1+ndvi))
    pix_val_ndvi.append((r, g, 0, 255))

img = Image.new("RGB", (width, height))
pixels = img.load()
for y in range(height):
    for x in range(width):
        pixels[x, y] = pix_val_ndvi[width*y+x]
img.save('/Users/sunny/Desktop/Programs/Anant Projects/Sentinel-2/NDVI/test-real-ndvi.png')
