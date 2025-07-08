# 🖼️ Image Processing Service (Spring Boot)

A minimal, extensible image processing API built with Spring Boot. Users can register, upload images, and apply transformations such as resizing, cropping, rotating, and grayscale filters. All files are stored locally with support for chained synchronous processing and JWT-based authentication.

---

## 🚀 Features

- ✅ **User Registration & Login** with JWT Authentication
- 📦 **Image Upload API** with size/format constraints (JPG, JPEG, PNG, etc.)
- 🧱 **Synchronous Image Transformations**:
  - Resize
  - Crop
  - Rotate
  - Grayscale / Sepia filters
  - Format conversion
- 🗂️ **Local Storage** with organized folder structure per user/image
- 📄 **RESTful API** design for easy integration with clients
- 🪵 Basic **logging** for debugging and development
- 🧪 Test locally — no deployment required

---

## 📁 Folder Structure

```
uploads/
└── {userId}/
    └── {imageId}/
        ├── original.jpg
        ├── resize_300x200.jpg
        └── transformed_grayscale_crop_100x100x10x10.jpg
```

---

## 🧠 How It Works

1. Clients register and log in via `/auth/register` and `/auth/login`.
2. Images are uploaded via `POST /images/upload` with JWT token in headers.
3. Clients can apply transformations with:
   ```http
   POST /images/{imageId}/transform
   ```
   Request Body:
   ```json
    {
      "resize": {
        "width": "100",
        "height": "100"
      },
      "crop": {
        "width": "10",
        "height": "10",
        "x": "0",
        "y": "0"
      },
      "filters": {
        "grayscale": true,
        "sepia": false
      },
      "rotate": 0,
      "format": "jpg"
    }
   ```
4. The server applies transformations synchronously and returns a path to the processed image.

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot**
- **JWT (jjwt)** for authentication
- **Lombok** for clean entity modeling
- **BufferedImage / Graphics2S** for image processing
- **Maven** for dependency management

---

## ⚙️ Configuration

Set maximum file size and supported formats in `application.properties`:

```properties
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
app.supported-formats=jpg,jpeg,png
```

---

## 🧪 Getting Started

```bash
git clone https://github.com/your-username/image-processing-service.git
cd image-processing-service
./mvnw spring-boot:run
```

Use Postman/Insomnia or cURL to test the endpoints locally.

---

## 🔒 Authentication

Use the `/auth/login` endpoint to receive a JWT token. Pass it in the `Authorization` header:

```
Authorization: Bearer <your-token>
```

---

## 🧹 Future Enhancements

- [ ] Redis-based caching for transformation results
- [ ] Async processing for heavy tasks
- [ ] Cloud storage support (S3, GCS)
- [ ] Web UI for image previews

---

## 📫 Feedback & Contributions

This project is a solo learning journey. Feel free to fork, play around, and open issues with ideas or bugs. No PRs accepted yet, but happy to discuss!

---
