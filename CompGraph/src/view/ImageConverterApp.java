package view;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import java.io.File;
import javafx.scene.input.MouseEvent;

public class ImageConverterApp extends Application {

	private Image originalImage;
    private ImageView originalImageView;
    private ImageView processedImageView;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Create layout components
        VBox root = new VBox(10);
        HBox imageBox = new HBox(10);
        
        originalImageView = new ImageView();
        processedImageView = new ImageView();

        // Button to upload an image
        Button uploadButton = new Button("Upload Image");
        uploadButton.setOnAction(e -> uploadImage(primaryStage));

        // ChoiceBox to select effect (grayscale or blur)
        ChoiceBox<String> effectChoiceBox = new ChoiceBox<>();
        effectChoiceBox.getItems().addAll("Grayscale", "Blur");
        effectChoiceBox.setValue("Grayscale");

        // Button to apply the selected effect
        Button applyButton = new Button("Apply Effect");
        applyButton.setOnAction(e -> applyEffect(effectChoiceBox.getValue()));

        // Add the UI components to the layout
        imageBox.getChildren().addAll(originalImageView, processedImageView);
        root.getChildren().addAll(uploadButton, effectChoiceBox, applyButton, imageBox);
        
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Image Converter");
        primaryStage.setScene(scene);
        primaryStage.show();
		
	}
	
	private void uploadImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            originalImage = new Image(file.toURI().toString());
            originalImageView.setImage(originalImage);
        } else {
            showAlert("No file selected", "Please select a valid image file.");
        }
    }

	private void applyEffect(String effect) {
        if (originalImage != null) {
            Image processedImage = originalImage;
            switch (effect) {
                case "Grayscale":
                    processedImage = applyGrayscale(originalImage);
                    break;
                case "Blur":
                    processedImage = applyBlur(originalImage);
                    break;
            }
            processedImageView.setImage(processedImage);
        } else {
            showAlert("No image", "Please upload an image first.");
        }
    }

    private Image applyGrayscale(Image image) {
        // Get pixel data from the image
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        PixelReader reader = image.getPixelReader();
        
        // Create a writable image to store the processed pixels
        javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(width, height);
        PixelWriter writer = writableImage.getPixelWriter();
        
        // Iterate over all pixels and apply grayscale
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Read the color of the current pixel
                javafx.scene.paint.Color color = reader.getColor(x, y);
                
                // Calculate luminance (grayscale)
                double gray = 0.2989 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
                
                // Create a new grayscale color
                javafx.scene.paint.Color grayColor = new javafx.scene.paint.Color(gray, gray, gray, color.getOpacity());
                
                // Write the new color to the pixel
                writer.setColor(x, y, grayColor);
            }
        }
        
        return writableImage;
    }

    private Image applyBlur(Image image) {
        // Get pixel data from the image
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        PixelReader reader = image.getPixelReader();
        
        // Create a writable image to store the processed pixels
        javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(width, height);
        PixelWriter writer = writableImage.getPixelWriter();
        
        // Apply a simple 3x3 blur kernel
        int[][] kernel = {
            {1, 2, 1},
            {2, 4, 2},
            {1, 2, 1}
        };
        int kernelSum = 16;  // Sum of all elements in the kernel
        
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                double red = 0, green = 0, blue = 0;
                
                // Apply the kernel to the surrounding pixels
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        javafx.scene.paint.Color color = reader.getColor(x + dx, y + dy);
                        red += color.getRed() * kernel[dx + 1][dy + 1];
                        green += color.getGreen() * kernel[dx + 1][dy + 1];
                        blue += color.getBlue() * kernel[dx + 1][dy + 1];
                    }
                }
                
                // Normalize the values
                red /= kernelSum;
                green /= kernelSum;
                blue /= kernelSum;
                
                // Write the new color to the pixel
                writer.setColor(x, y, new javafx.scene.paint.Color(red, green, blue, 1.0));
            }
        }
        
        return writableImage;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
