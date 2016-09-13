# BikeTracker

This is an Android Application to track your sports activities with a special focus on
bikes. The Application is completely open source and all your data stays in your hands.
You can export your tracks to files or (with a future release) use web based services
to publish your efforts. The program uses some interesting algorithms to filter low
quality altitude measurements from the GPS or to calculate the distance between geographic
coordinates.

## Features

* Track your sport activities
* Overview over your last activities with filters
* Statistics about a single track or multiple ones
* Export your tracks with kml/gpx files

## Getting started

Currently there are no official releases yet. In order to get the Application on
your phone you have to clone the github repository and use [Android Studio](https://developer.android.com/studio/index.html)
to build and deploy the App.

## Development

Brief overview over the development process.

### Repositories
The [github repository](https://github.com/crapp/biketracker) of biketracker has
several different branches.

* master      : Main development branch. Everything in here is guaranteed to
compile and is tested. This is the place for new features and bugfixes. Pull requests welcome.
* dev         : Test branch and wild west area. May not compile.
* release-x.x : Branch for a release. Only bugfixes are allowed here. Pull requests welcome.
* gh-pages    : Special branch for static HTML content hosted by github.io.


### Versioning

I decided to use [semantic versioning](http://semver.org/)

## Bugs, feature requests, ideas

Please use the [github bugtracker](https://github.com/crapp/biketracker/issues)
to submit bugs or feature requests

## ToDo

Have a look in the todo folder. I am using the [todo.txt](http://todotxt.com/)
format for my todo lists.

## License
```
Copyright (C) 2015 - 2016 Christian Rapp

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```

Have a look into the license sub folder where license files of the used libraries
are located.


