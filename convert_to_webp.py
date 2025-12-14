"""
Convert PNG images to WebP format for APK size reduction
"""
from PIL import Image
import os

# Define the res directory
res_dir = r"c:\Users\abhay\repos\finalbenchmark-platform\app\src\main\res"

# Images to convert in drawable folder
drawable_images = [
    "logo.png",
    "ic_launcher_foreground.png",
    "mascot.png",
    "me.png",
    "logo_2.png"
]

def convert_to_webp(input_path, output_path, quality=90):
    """Convert PNG to WebP with specified quality"""
    try:
        img = Image.open(input_path)
        # Convert RGBA to RGB if necessary (WebP supports both)
        if img.mode in ('RGBA', 'LA'):
            # Keep alpha channel for WebP
            img.save(output_path, 'WEBP', quality=quality, method=6)
        else:
            img.save(output_path, 'WEBP', quality=quality, method=6)
        
        # Get file sizes
        original_size = os.path.getsize(input_path) / (1024 * 1024)  # MB
        new_size = os.path.getsize(output_path) / (1024 * 1024)  # MB
        reduction = ((original_size - new_size) / original_size) * 100
        
        print(f"✓ Converted: {os.path.basename(input_path)}")
        print(f"  Original: {original_size:.2f} MB → WebP: {new_size:.2f} MB ({reduction:.1f}% reduction)")
        return True
    except Exception as e:
        print(f"✗ Failed to convert {input_path}: {e}")
        return False

def main():
    print("Converting PNG images to WebP format...\n")
    
    # Convert drawable images
    drawable_dir = os.path.join(res_dir, "drawable")
    for img_name in drawable_images:
        input_path = os.path.join(drawable_dir, img_name)
        output_path = os.path.join(drawable_dir, img_name.replace('.png', '.webp'))
        
        if os.path.exists(input_path):
            convert_to_webp(input_path, output_path)
        else:
            print(f"✗ File not found: {input_path}")
    
    # Convert mipmap launcher icons
    print("\nConverting launcher icons in mipmap folders...")
    mipmap_folders = ['mipmap-mdpi', 'mipmap-hdpi', 'mipmap-xhdpi', 'mipmap-xxhdpi', 'mipmap-xxxhdpi']
    icon_names = ['ic_launcher.png', 'ic_launcher_round.png']
    
    for folder in mipmap_folders:
        folder_path = os.path.join(res_dir, folder)
        if os.path.exists(folder_path):
            for icon in icon_names:
                input_path = os.path.join(folder_path, icon)
                output_path = os.path.join(folder_path, icon.replace('.png', '.webp'))
                if os.path.exists(input_path):
                    convert_to_webp(input_path, output_path)
    
    print("\n✓ Conversion complete!")

if __name__ == "__main__":
    main()
