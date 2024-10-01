import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css'],
  standalone: true
})
export class UploadComponent {
  selectedFile: File | null = null;

  constructor(private http: HttpClient) { }

  onFileSelected(event: any) {
    console.log("entering file selected");
    this.selectedFile = event.target.files[0];
    console.log(this.selectedFile);
  }

  uploadFile() {
    const selectedFile = this.selectedFile;
    if (!selectedFile) {
      console.log('No file selected.');
      return;
    }


    // if (!selectedFile.name.match(/.(jpg|jpeg|png|gif)$/i)) {
    //   console.log('Selected file is invalid.');
    //   return;
    // }

    // Request the pre-signed URL from the back-end
    this.http.get(`http://localhost:5000/generate-presigned-url?fileName=${selectedFile.name}`, { responseType: 'text' })
      .subscribe(
        (presignedUrl: string) => {
          // Upload the file to S3 using the pre-signed URL
          this.http.put(presignedUrl, selectedFile, {
            headers: { 'Content-Type': selectedFile.type }
          }).subscribe(
            () => console.log('File uploaded successfully!'),
            error => console.error('Error uploading file:', error)
          );
        },
        error => console.error('Error generating pre-signed URL:', error)
      );
  }
}