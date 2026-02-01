#!/usr/bin/env python3
"""Script to replace color #3A0000 with #950000 in all PNG files."""

from PIL import Image
import os

def replace_color(image_path, old_color, new_color):
    """Replace old_color with new_color in the image."""
    img = Image.open(image_path).convert('RGBA')
    pixels = img.load()
    
    changed = False
    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = pixels[x, y]
            if (r, g, b) == old_color:
                pixels[x, y] = (new_color[0], new_color[1], new_color[2], a)
                changed = True
    
    if changed:
        img.save(image_path)
        print(f"Updated: {image_path}")
    else:
        print(f"No changes: {image_path}")
    
    return changed

# Colors in RGB format
old_color = (0x3A, 0x00, 0x00)  # #3A0000
new_color = (0x95, 0x00, 0x00)  # #950000

texture_dir = r"c:\Users\nedza\Desktop\CreatePhotomovement\neoforge\1211\src\main\resources\assets\createphotomovement\textures\block"

updated_count = 0
for filename in os.listdir(texture_dir):
    if filename.endswith('.png'):
        filepath = os.path.join(texture_dir, filename)
        if replace_color(filepath, old_color, new_color):
            updated_count += 1

print(f"\nTotal files updated: {updated_count}")
