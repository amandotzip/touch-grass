import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

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
    console.log('PhotoGalleryComponent initialized');
    this.http.get<string[]>('http://localhost:5000/get-s3-images')
      .subscribe(data => {
        console.log(data);
        this.images = data;
      });
  }
}