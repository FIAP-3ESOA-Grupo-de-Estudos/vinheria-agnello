from PIL import Image
import os

def split_image(image_path, rows, cols):
    img = Image.open(image_path)
    img_width, img_height = img.size
    
    # Calcula as dimensões de cada pedaço
    width = img_width // cols
    height = img_height // rows
    
    base_name = os.path.splitext(image_path)[0]
    
    count = 0
    for r in range(rows):
        for c in range(cols):
            # Define as coordenadas do corte (left, upper, right, lower)
            box = (c * width, r * height, (c + 1) * width, (r + 1) * height)
            part = img.crop(box)

            # converte para RGB para aceitar o formato JPEG
            if part.mode in ("RGBA", "P"):
                part = part.convert("RGB")
                
            part.save(f"{base_name}_crop_{count}.jpg", "JPEG")
            count += 1

# Executar para os dois arquivos
for filename in ["src/main/webapp/assets/images/products-images-1.png", "src/main/webapp/assets/images/products-images-2.png"]:
    split_image(filename, 2, 3)