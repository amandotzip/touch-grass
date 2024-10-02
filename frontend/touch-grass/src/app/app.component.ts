import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UploadComponent } from './upload-component/upload.component';
import { HttpClientModule } from '@angular/common/http';
import { PhotoGalleryComponent } from './photo-gallery-component/photo-gallery.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, UploadComponent, HttpClientModule, PhotoGalleryComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'touch-grass';
}
