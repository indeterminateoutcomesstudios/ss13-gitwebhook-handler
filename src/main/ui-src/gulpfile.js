const gulp = require('gulp');
const sass = require('gulp-sass');
const minifyCss = require('gulp-minify-css');
const rename = require('gulp-rename');
const uglify = require('gulp-uglify');
const concat = require('gulp-concat');
const babel = require('gulp-babel');
const del = require('del');

gulp.task('clean', () => del('./dist'));

gulp.task('js', ['clean'], () => gulp.src('.js/**/*.js')
    .pipe(babel({presets: ['env']}))
    .pipe(uglify())
    .pipe(rename({suffix: '.min'}))
    .pipe(gulp.dest('./dist')));

gulp.task('scss-config', ['clean'], () => {
    gulp.src('.scss/config-scss/**/*.scss')
        .pipe(sass())
        .pipe(concat('config-style.min.css'))
        .pipe(minifyCss())
        .pipe(gulp.dest('./dist'));
});

gulp.task('scss-common', ['clean'], () => {
    gulp.src('.scss/common/**/*.scss')
        .pipe(sass())
        .pipe(minifyCss())
        .pipe(rename({suffix: '.min'}))
        .pipe(gulp.dest('./dist'));
});

gulp.task('default', ['clean', 'js', 'scss-config', 'scss-common']);