import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-nearby-locations',
  templateUrl: './nearby-locations.component.html',
  styleUrls: ['./nearby-locations.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class NearbyPlacesComponent implements OnInit {
  location: string | null = null;
  places: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get('https://ipapi.co/json/')
      .subscribe((data: any) => {
        this.location = `${data.city}, ${data.region}`;
        console.log(this.location);
        // this.http.get(`https://your-places-api.com/nearby?city=${data.city}`)
        //   .subscribe((placesData: any) => {
        //     this.places = placesData;
        //   });
      });
  }
}