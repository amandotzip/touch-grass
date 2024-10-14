import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';


@Component({
    selector: 'app-title-section',
    templateUrl: './title-section.component.html',
    styleUrls: ['./title-section.component.css'],
    standalone: true,
    imports: [CommonModule]
})
export class TitleSectionComponent implements OnInit {

    constructor(private http: HttpClient) { }
    ngOnInit(): void {

    }

}