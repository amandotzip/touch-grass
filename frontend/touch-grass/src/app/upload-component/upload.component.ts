declare var grecaptcha: any;

import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css'],
  standalone: true
})
export class UploadComponent {
  selectedFile: File | null = null;
  recaptchaToken: string | null = null;

  constructor(private http: HttpClient) { }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
    console.debug(this.selectedFile);
  }


  triggerRecaptchaAndUpload() {

    // Ensure that a file has been selected before proceeding
    if (!this.selectedFile) {
      console.log('No file selected.');
      return;
    }
    // Trigger Google reCAPTCHA v3 to get the token
    grecaptcha.ready(() => {
      grecaptcha.execute(environment.recaptchaSiteKey, { action: 'upload' }).then((token: string) => {
        // Assign the generated token to a variable
        this.recaptchaToken = token;
        // Call the upload function once the reCAPTCHA is completed
        this.uploadFile();
      });
    });
  }

  uploadFile() {

    if (!this.recaptchaToken) {
      console.log('reCAPTCHA validation failed in uploadFile().');
      return;
    }
    const selectedFile = this.selectedFile;
    if (!selectedFile) {
      console.log('No file selected.');
      return;
    }


    if (!selectedFile.name.match(/.(jpg|jpeg|png|gif)$/i)) {
      console.log('Selected file is invalid.');
      return;
    }

    // Request the pre-signed URL from the back-end
    this.http.get(`${environment.apiBaseUrl}/generate-presigned-url?fileName=${selectedFile.name}&recaptchaToken=${this.recaptchaToken}`, { responseType: 'text' })
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