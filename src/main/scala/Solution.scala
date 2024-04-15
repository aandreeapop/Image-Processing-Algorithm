import util.{Pixel, Util}

import scala.annotation.tailrec

// Online viewer: https://0xc0de.fr/webppm/
object Solution {
  type Image = List[List[Pixel]]
  type GrayscaleImage = List[List[Double]]

  def split(delim: Char)(image: List[Char]): List[List[Char]] = {
    def op(c: Char, acc: List[List[Char]]): List[List[Char]] =
      acc match {
        case Nil => if (c == delim) Nil else List(List(c))
        case x :: xs => if (c == delim) Nil :: acc else (c :: x) :: xs
      }

    image.foldRight(Nil: List[List[Char]])(op)
  }

  def makeMatrixFromList[A](list: List[A], linii: Int, coloane: Int): List[List[A]] = {
    if (linii == 0) Nil
    else list.splitAt(coloane)._1 :: makeMatrixFromList(list.splitAt(coloane)._2, linii - 1, coloane)
  }

  // prerequisites
  def fromStringPPM(image: List[Char]): Image = {
    val splitImage = split('\n')(image)

    def op(l: List[Char], acc: List[Pixel]): List[Pixel] = {
      val pixel = split(' ')(l)
      acc match
        case Nil => List(Pixel(pixel.head.mkString.toInt, pixel(1).mkString.toInt, pixel(2).mkString.toInt))
        case x :: xs => Pixel(pixel.head.mkString.toInt, pixel(1).mkString.toInt, pixel(2).mkString.toInt) :: x :: xs
    }

    val listOfPixels = splitImage.drop(3).foldRight(Nil)(op)

    val marimeImg = split(' ')(splitImage(1))
    val coloane = marimeImg.head.mkString.toInt
    val linii = marimeImg(1).mkString.toInt

    makeMatrixFromList[Pixel](listOfPixels,linii, coloane)
  }

  def toStringPPM(image: Image): List[Char] = {
    val linii = image.size
    val coloane = image.head.size

    def pixelToString(pixel: Pixel): List[Char] = {
      List('\n') ++ pixel.red.toString ++ List(' ') ++ pixel.green.toString ++ List(' ') ++ pixel.blue.toString
    }

    def makeString(listOfPixels: List[Pixel]): List[Char] = {
      listOfPixels.flatMap(pixelToString)
    }

    val acc = List('P', '3', '\n') ++ coloane.toString ++ List(' ') ++ linii.toString
      ++ List('\n', '2', '5', '5')

    acc ++ makeString(image.flatten) ++ List('\n')
  }

  // ex 1
  def verticalConcat(image1: Image, image2: Image): Image = {
    image1 ++ image2
  }

  // ex 2
  def horizontalConcat(image1: Image, image2: Image): Image = {
    image1.zip(image2).map(pair => pair._1 ++ pair._2)
  }

  // ex 3
  def rotateOnce(image: Image): Image = {
    image match
      case Nil :: _ => Nil
      case _ => image.map(_.takeRight(1).head) :: rotateOnce(image.map(_.dropRight(1)))
  }

  @tailrec
  def rotate(image: Image, degrees: Integer): Image = {
    if (degrees == 360 || degrees == 0) image
    else rotate(rotateOnce(image), degrees - 90)
  }

  // ex 4
  val gaussianBlurKernel: GrayscaleImage = List[List[Double]](
    List( 1, 4, 7, 4, 1),
    List( 4,16,26,16, 4),
    List( 7,26,41,26, 7),
    List( 4,16,26,16, 4),
    List( 1, 4, 7, 4, 1)
  ).map(_.map(_ / 273))

  val Gx : GrayscaleImage = List(
    List(-1, 0, 1),
    List(-2, 0, 2),
    List(-1, 0, 1)
  )

  val Gy : GrayscaleImage = List(
    List( 1, 2, 1),
    List( 0, 0, 0),
    List(-1,-2,-1)
  )

  val whitePixel : Pixel = Pixel(255, 255, 255)
  val blackPixel : Pixel = Pixel(0, 0, 0)

  def toGrayScaleImage(image: Image): GrayscaleImage = {
    image.map(_.map(Util.toGrayScale))
  }

  def edgeDetection(image: Image, threshold : Double): Image = {
    val grayscaleImage = toGrayScaleImage(image)
    val blurredImage = applyConvolution(grayscaleImage, gaussianBlurKernel)
    val Mx = applyConvolution(blurredImage, Gx)
    val My = applyConvolution(blurredImage, Gy)
    val MxMyList = Mx.flatten.zip(My.flatten).map(pair => (pair._1.abs + pair._2.abs))
    val MxMy = makeMatrixFromList[Double](MxMyList, Mx.size, Mx.head.size)

    def verifyThresholdOneLine(line: List[Double]): List[Pixel] = {
      line match
        case Nil => Nil
        case x :: xs =>
          if (x < threshold) blackPixel :: verifyThresholdOneLine(xs)
          else whitePixel :: verifyThresholdOneLine(xs)
    }

    def verifyThreshold(matrix: GrayscaleImage): Image = {
      matrix match
        case Nil => Nil
        case x :: xs => verifyThresholdOneLine(x) :: verifyThreshold(xs)
    }

    verifyThreshold(MxMy)
  }

  def applyConvolution(image: GrayscaleImage, kernel: GrayscaleImage) : GrayscaleImage = {
    def newImagePixel(image1: GrayscaleImage, image2: GrayscaleImage): Double = {
      val listOfDouble = image1.flatten.zip(image2.flatten).map(pair => pair._1 * pair._2)
      listOfDouble.sum
    }

    val matrixOfNeighbours = Util.getNeighbors(image, kernel.size/2)
    val convolutionLines = matrixOfNeighbours.size
    val convolutionColumns = matrixOfNeighbours.head.size
    def makeListOfConvolution (listofNeighbours: List[GrayscaleImage]): List[Double] = {
      listofNeighbours.map(newImagePixel(_, kernel))
    }
    makeMatrixFromList[Double](makeListOfConvolution(matrixOfNeighbours.flatten), convolutionLines, convolutionColumns)
  }

  // ex 5
  @tailrec
  def pascalTriangle(size: Integer, l: Integer, c: Integer, accLine: List[BigInt], accTriangle: List[List[BigInt]]): List[List[BigInt]] = {
    if (l > size - 1) accTriangle
    else if (c > size - 1) pascalTriangle(size, l + 1, 0, Nil, accTriangle ++ List(accLine))
    else if (c > l) pascalTriangle(size, l, c + 1, accLine ++ List(-1), accTriangle)
    else if (c == 0 || c == l) pascalTriangle(size, l, c + 1, accLine ++ List(1), accTriangle)
    else pascalTriangle(size, l, c + 1, accLine ++ List(accTriangle(l - 1)(c - 1) + accTriangle(l - 1)(c)), accTriangle)
  }

  def moduloPascal(m: Integer, funct: Integer => Pixel, size: Integer): Image = {
    def pickAllColors(i: Int): Pixel = {
      if (i == -1) Pixel(0, 0, 0)
      else funct(i)
    }

    def firstLine(size: Integer): List[BigInt] = {
      if(size == 1) List(1)
      else firstLine(size - 1) ++ List(-1)
    }

    val triangle = List(firstLine(size))


    val pascalTriangleModulo = pascalTriangle(size, 1, 0, Nil, triangle).map(_.map(x => (x % m.toInt).toInt))
    pascalTriangleModulo.map(_.map(pickAllColors))
  }
}
