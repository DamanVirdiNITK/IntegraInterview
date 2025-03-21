import fitz  # PyMuPDF for extracting text from PDFs
import openai  # OpenAI API for structuring text
import json  # To save structured data
import pytesseract
from PIL import Image
import cv2
from pdf2image import convert_from_path

# Set OpenAI API key (Replace with your key or use an environment variable)
openai.api_key = "your_openai_api_key"

def extract_text_from_pdf(pdf_path):
    """
    Extracts text from a given PDF file.
    
    Args:
        pdf_path (str): Path to the PDF file.
    
    Returns:
        str: Extracted text from the PDF.
    """
    doc = fitz.open(pdf_path)  # Open the PDF file
    text = ""
    for page in doc:  # Iterate through all pages
        text += page.get_text("text") + "\n"  # Extract text
    return text.strip()  # Return cleaned-up text

def extract_text_from_pdf_ocr(pdf_path):
    pages = convert_from_path('pdf_path.pdf', 300)  # 300 DPI
  
    # Apply OCR to the first page of the PDF
    first_page = pages[0]
    text  = pytesseract.image_to_string(first_page)
    return text.strip()
    

def structure_text_with_ai(text):
    """
    Sends extracted text to GPT-4 and returns structured data in JSON format.
    
    Args:
        text (str): Extracted text from the PDF.
    
    Returns:
        str: AI-processed structured JSON data.
    """
    prompt = f"Extract structured data from the following text and return JSON:\n\n{text}"
    response = openai.ChatCompletion.create(
        model="gpt-4",
        messages=[{"role": "user", "content": prompt}]
    )
    return response["choices"][0]["message"]["content"]  # Return AI response


def save_json(data, output_path):
    """
    Saves structured data in a JSON file.
    
    Args:
        data (dict): Structured data to save.
        output_path (str): Path for the output JSON file.
    """
    with open(output_path, "w") as f:
        json.dump(data, f, indent=4)  # Write data to a JSON file

# File paths
pdf_path = "sample.pdf"  # Path to the input PDF file
output_json = "structured_data.json"  # Path to output JSON file

# Workflow Execution
text = extract_text_from_pdf(pdf_path)  # Step 1: Extract text
text = extract_text_from_pdf_ocr(pdf_path) # Step 1.1 : Extract text with OCR
structured_data = structure_text_with_ai(text)  # Step 2: Process text with AI
save_json(json.loads(structured_data), output_json)  # Step 3: Save structured data

print("Structured data saved to", output_json)
