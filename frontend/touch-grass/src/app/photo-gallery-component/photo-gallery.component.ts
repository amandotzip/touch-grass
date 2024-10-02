import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-photo-gallery',
  templateUrl: './photo-gallery.component.html',
  styleUrls: ['./photo-gallery.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class PhotoGalleryComponent implements OnInit {
  images: string[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<string[]>(`${environment.apiBaseUrl}/get-s3-images`)
      .subscribe(data => {
        console.debug(data);
        this.images = data;
      });
  }
}